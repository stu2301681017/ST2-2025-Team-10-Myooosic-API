package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.app.ai.AIResponseException;
import io.github.stu2301681017.MyooosicAPI.app.ai.AIUnavailableException;
import io.github.stu2301681017.MyooosicAPI.app.song.SongService;
import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class QueryService {

    private final ChatModel model;
    private final SongService songService;
    private final Validator validator;

    public QueryService(
            ChatModel model,
            SongService songService,
            Validator validator
    ) {
        this.model = model;
        this.songService = songService;
        this.validator = validator;
    }

    public Collection<Suggestion> getSongSuggestionsForPrompt(@Valid @NotNull QueryPrompt prompt, @Positive int amount) {
        QuerySuggestion[] answer;
        try {
            answer = ChatClient.create(model)
                    .prompt()
                    .user(u -> u
                            .text("Find me {amount} songs (that can be found on deezer) that fit the following keywords, and add a short (128 chars or less) reason why it fits: {keywords}")
                            .param("keywords", prompt.query())
                            .param("amount", amount))
                    .call()
                    .entity(QuerySuggestion[].class);

            validator.validate(answer);

        } catch (NonTransientAiException e) {
            throw new AIUnavailableException("AI service unavailable", e);
        } catch (TransientAiException e) {
            throw new AIResponseException("AI service returned transient error", e);
        } catch (ConstraintViolationException e) {
            throw new AIResponseException("AI service failed to follow constraints", e);
        }

        return Arrays.stream(answer)
                .map(rawSuggestion -> {
                    Song song = songService.getSongFromIdentifier(rawSuggestion.identifier());
                    return new Suggestion(song, prompt, rawSuggestion.reason());
                })
                .collect(Collectors.toList());

    }

}
