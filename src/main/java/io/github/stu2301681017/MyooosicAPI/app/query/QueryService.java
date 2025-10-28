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
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class QueryService {

    private final ChatClient chat;
    private final SongService songService;
    private final Validator validator;

    public QueryService(
            ChatClient.Builder chatBuilder,
            ChatModel model,
            SongService songService,
            Validator validator
    ) {
        this.chat = chatBuilder.defaultAdvisors(
                ChatModelCallAdvisor
                        .builder()
                        .chatModel(model)
                        .build()
        ).build();
        this.songService = songService;
        this.validator = validator;
    }

    public Collection<Suggestion> getSongSuggestionsForPrompt(@Valid @NotNull QueryPrompt prompt, @Positive int amount) {
        QuerySuggestion[] answer;
        try {
            ChatClient.CallResponseSpec spec = chat
                .prompt()
                .user(u -> u
                        .text("""
                          Find {amount} songs (that can be found on Deezer) that fit
                          the following keywords and a small (<=128 characters) reason why.
                          Be varied and do not ever repeat the same song twice, shuffle heavily.
                          Return as JSON in the following schema:
                          A single property "items" which contains an array of objects
                          that each have a property "identifier", which has two strings "name" and "author";
                          and a property "reason".
                          Keywords:
                          {keywords}
                        """)

                        .param("keywords", prompt.query())
                        .param("amount", amount))
                .call();

            answer = Objects.requireNonNull(spec.entity(new ParameterizedTypeReference<QuerySuggestionAnswer>() {})).items();

            System.out.println(spec.content());

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
