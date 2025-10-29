package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.app.ai.*;
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

    public QueryService(
            ChatClient.Builder chatBuilder,
            ChatModel model,
            SongService songService,
            Validator validator
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
    }

    public Collection<Suggestion> getSongSuggestionsForPrompt(@Valid @NotNull QueryPrompt prompt, @Positive int amount, List<SongIdentifier> avoid) {

        List<Suggestion> suggestions = new ArrayList<>();
        List<SongIdentifier> avoidAndAlreadyTaken = new ArrayList<>(avoid);

        for (int i = 0; i < amount; i++) {
            Suggestion suggestion = suggest(prompt, avoidAndAlreadyTaken, 0);
            if (suggestion != null) {
                avoidAndAlreadyTaken.add(suggestion.song().id());
            }
            suggestions.add(suggestion);
        }

        return suggestions;
    }

    private Suggestion suggest(@Valid @NotNull QueryPrompt prompt, List<SongIdentifier> avoid, @PositiveOrZero int tries) {

        String failedSuggestionsAsString = avoid
                .stream()
                .map(avoidSong -> avoidSong.name() + ", by " + avoidSong.author())
                .collect(Collectors.joining("; "));
        Logger.getGlobal().info("Trying a new suggestion for prompt '" + prompt.query() + "'. Try " + tries + ". Avoiding suggestions: " + failedSuggestionsAsString);

        QuerySuggestion suggestionAttempt = suggestionRun(prompt, avoid);
        try {
            Song suggestedSong = tryGetSongFromSuggestion(suggestionAttempt);
            Logger.getGlobal().info("Found a song with name " + suggestionAttempt.identifier().name() + " by author " + suggestionAttempt.identifier().author() + ", returning.");
            return new Suggestion(suggestedSong, prompt, suggestionAttempt.reason());
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

    private QuerySuggestion suggestionRun(@Valid @NotNull QueryPrompt prompt, List<SongIdentifier> failedSuggestions) {
        QuerySuggestion answer;

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
                    .entity(QuerySuggestion.class);

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

    private Song tryGetSongFromSuggestion(QuerySuggestion suggestion) throws AIHallucinationException {
        return songService
                .getSongsFromIdentifier(suggestion.identifier())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AIHallucinationException("AI responded with a song that doesn't exist"));
    }

}
