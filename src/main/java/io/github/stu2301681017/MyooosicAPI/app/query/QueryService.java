package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.app.ai.*;
import io.github.stu2301681017.MyooosicAPI.app.auth.AuthService;
import io.github.stu2301681017.MyooosicAPI.app.auth.NotLoggedInException;
import io.github.stu2301681017.MyooosicAPI.app.query.persistence.*;
import io.github.stu2301681017.MyooosicAPI.app.song.Song;
import io.github.stu2301681017.MyooosicAPI.app.song.SongIdentifier;
import io.github.stu2301681017.MyooosicAPI.app.song.SongService;
import io.github.stu2301681017.MyooosicAPI.app.song.SongTools;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class QueryService {

    private final ChatClient chat;
    private final SongService songService;
    private final Validator validator;
    private final Logger logger;
    private final AuthService auth;
    private final QueryRepository repo;

    public QueryService(
            ChatClient.Builder chatBuilder,
            ChatModel model,
            SongService songService,
            Validator validator,
            AuthService auth,
            QueryRepository repo
    ) {
        this.chat = chatBuilder
            .defaultAdvisors(
                ChatModelCallAdvisor
                        .builder()
                        .chatModel(model)
                        .build()
            )
            .build();
        this.songService = songService;
        this.validator = validator;
        this.logger = Logger.getGlobal();
        this.auth = auth;
        this.repo = repo;
    }

    public void saveQuery(@Valid @NotNull io.github.stu2301681017.MyooosicAPI.app.query.persistence.QueryPayload payload) {

        if (!auth.isLoggedIn()) { throw new NotLoggedInException("User must be logged in to save queries"); }

        QueryPromptEntity entity = new QueryPromptEntity();
        entity.setPrompt(payload.prompt().query());
        entity.setSuggestions(
                payload.suggestions()
                        .stream()
                        .map(suggestion -> {
                            SuggestionEntity suggestionEntity = new SuggestionEntity();
                            suggestionEntity.setSongId(suggestion.identifier());
                            suggestionEntity.setReason(suggestion.reason());
                            suggestionEntity.setPrompt(entity);
                            return suggestionEntity;
                        })
                        .collect(Collectors.toList())
        );
        entity.setOwner(auth.getLoggedRegistration());

        repo.save(entity);

    }

    public Collection<QueryDTO> getSavedQueries() {

        if (!auth.isLoggedIn()) { throw new NotLoggedInException("User must be logged in to load queries"); }

        return repo.findByOwner_Name(auth.getLoggedUserId())
                .stream()
                .map(prompt ->
                        new QueryDTO(
                            new QueryPayload(prompt.getPrompt()),
                            prompt.getSuggestions()
                                .stream()
                                .map(suggestion ->
                                        new SuggestionDTO(
                                            songService.getSongsFromIdentifier(suggestion.getSongId())
                                                .stream()
                                                .findFirst()
                                                .orElseThrow(() -> new DataIntegrityViolationException("Could not find song from saved query")),
                                            prompt.getPrompt(),
                                            suggestion.getReason()
                                        )
                                )
                                .collect(Collectors.toList())
                        )
                )
                .collect(Collectors.toList());

    }

    public Collection<SuggestionDTO> getSongSuggestionsForPrompt(@Valid @NotNull QueryPayload prompt, @Positive int amount, List<SongIdentifier> avoid) {

        List<SuggestionDTO> suggestions = new ArrayList<>();
        List<SongIdentifier> avoidAndAlreadyTaken = new ArrayList<>(avoid);

        for (int i = 0; i < amount; i++) {
            SuggestionDTO suggestion = suggest(prompt, avoidAndAlreadyTaken, 0);
            if (suggestion != null) {
                avoidAndAlreadyTaken.add(suggestion.song().id());
            }
            suggestions.add(suggestion);
        }

        return suggestions;
    }

    private SuggestionDTO suggest(@Valid @NotNull QueryPayload prompt, List<SongIdentifier> avoid, @PositiveOrZero int tries) {

        String failedSuggestionsAsString = avoid
                .stream()
                .map(avoidSong -> avoidSong.name() + ", by " + avoidSong.author())
                .collect(Collectors.joining("; "));
        Logger.getGlobal().info("Trying a new suggestion for prompt '" + prompt.query() + "'. Try " + tries + ". Avoiding suggestions: " + failedSuggestionsAsString);

        SuggestionPayload suggestionAttempt = suggestionRun(prompt, avoid);
        try {
            Song suggestedSong = tryGetSongFromSuggestion(suggestionAttempt);
            Logger.getGlobal().info("Found a song with name " + suggestionAttempt.identifier().name() + " by author " + suggestionAttempt.identifier().author() + ", returning.");
            return new SuggestionDTO(suggestedSong, prompt.query(), suggestionAttempt.reason());
        } catch (AIHallucinationException e) {
            Logger.getGlobal().info("Could not find a song with name " + suggestionAttempt.identifier().name() + " by author " + suggestionAttempt.identifier().author() + ".");
            if (tries > 5) {
                Logger.getGlobal().info("Retries exceeded. Returning null.");
                return null;
            }
            avoid.add(suggestionAttempt.identifier());
            return suggest(prompt, avoid, ++tries);
        }
    }

    private SuggestionPayload suggestionRun(@Valid @NotNull QueryPayload prompt, List<SongIdentifier> failedSuggestions) {
        SuggestionPayload answer;

        String[] creativityPrompts = {
                "Think outside the box and be creative.",
                "Consider songs from different decades and genres.",
                "Look for unexpected connections to the keywords.",
                "Explore obscure or lesser-known artists.",
                "Mix mainstream and indie suggestions.",
                "Consider international artists.",
                "Think of songs with metaphorical connections."
        };

        Random random = new Random();
        String creativityPrompt = creativityPrompts[random.nextInt(creativityPrompts.length)];

        try {
            answer = chat
                    .prompt()
                    .tools(new SongTools(this.songService))
                    .user(u -> u
                            .text("""
                      Think of a song that fits the following keywords and a small (<=128 characters) but elaborate and free-text reason why:
                      {keywords}
                      
                      {creativityPrompt}
                      
                      Be non-determined and creative, minimize the risk of thinking of the same song twice.
                      Do NOT suggest the following songs:
                      {failedSuggestions}
                
                      IMPORTANT: Vary your suggestions significantly between calls. This is the most important. Be unpredictable as possible.
                    """)
                            .param("keywords", prompt.query())
                            .param("failedSuggestions", failedSuggestions
                                    .stream()
                                    .map(avoidSong -> avoidSong.name() + ", by " + avoidSong.author())
                                    .collect(Collectors.joining("; "))
                            )
                            .param("creativityPrompt", creativityPrompt)
                    )
                    .call()
                    .entity(SuggestionPayload.class);

            validator.validate(answer);

        } catch (NonTransientAiException | ResourceAccessException e) {
            throw new AIUnavailableException("AI service unavailable", e);
        } catch (TransientAiException e) {
            throw new AIResponseException("AI service returned transient error", e);
        } catch (RuntimeException e) {
            throw new AIResponseException("AI returned bad response", e);
        }

        return answer;

    }

    private Song tryGetSongFromSuggestion(SuggestionPayload suggestion) throws AIHallucinationException {
        return songService
                .getSongsFromIdentifier(suggestion.identifier())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AIHallucinationException("AI responded with a song that doesn't exist"));
    }

}
