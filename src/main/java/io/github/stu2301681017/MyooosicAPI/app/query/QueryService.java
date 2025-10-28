package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.app.ai.*;
import io.github.stu2301681017.MyooosicAPI.app.song.SongService;
import io.github.stu2301681017.MyooosicAPI.app.song.SongTools;
import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueryService {

    private final ChatClient chat;
    private final SongService songService;
    private final Validator validator;
    private final SongTools songTools;
    private final WebTools webTools;

    public QueryService(
            ChatClient.Builder chatBuilder,
            ChatModel model,
            SongService songService,
            Validator validator,
            ChatMemory memory,
            SongTools songTools,
            WebTools webTools
    ) {
        this.chat = chatBuilder
            .defaultAdvisors(
                ChatModelCallAdvisor
                        .builder()
                        .chatModel(model)
                        .build(),
                MessageChatMemoryAdvisor
                        .builder(memory)
                        .build()
            )
            .build();
        this.songService = songService;
        this.validator = validator;
        this.songTools = songTools;
        this.webTools = webTools;
    }

    public Collection<Suggestion> getSongSuggestionsForPrompt(@Valid @NotNull QueryPrompt prompt, @Positive int amount) {
        QuerySuggestionAnswer answer;
        try {
            ChatClient.CallResponseSpec spec = chat
                .prompt()
                .tools(songTools)
                .user(u -> u
                    .text("""
                      Find {amount} songs using your tools that fit
                      the following keywords and a small (<=128 characters) reason why.
                      Be varied and do not ever repeat the same song twice, shuffle heavily.
                      !!!MOST IMPORTANT:!!!
                      Although you should use all tools, please verify if a given song exists with the Song Service
                      with the given song name and author before listing it.
                      If it doesn't, discard it, try again.
                      DO NOT EVER INVENT SONGS, USE YOUR TOOLS TO VERIFY THEM!!!!!!!!!!
                      Keywords:
                      {keywords}
                    """)
                    .param("keywords", prompt.query())
                    .param("amount", amount))
                .call();

            answer = spec.entity(new ReliableBeanOutputConverter<>(QuerySuggestionAnswer.class,
                    new QuerySuggestionAnswer(
                            List.of(
                                    new QuerySuggestion(
                                            new SongIdentifier("songname", "authorname"),
                                            "Reason why it fits the keywords"
                                    )
                            )
                    )
            ));

            validator.validate(answer);

        } catch (NonTransientAiException e) {
            throw new AIUnavailableException("AI service unavailable", e);
        } catch (TransientAiException e) {
            throw new AIResponseException("AI service returned transient error", e);
        } catch (RuntimeException e) {
            throw new AIResponseException("AI returned bad response", e);
        }

        /*
            Although the LLM is perfectly capable of querying the Song Service by itself,
            we protect ourselves against hallucinations by reading only the identifiers of songs,
            and fetching all song data (including long CDN urls) ourselves.
         */
        return answer.items().stream()
                .map(rawSuggestion -> {
                    Song song = songService
                            .getSongsFromIdentifier(rawSuggestion.identifier())
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new AIHallucinationException("AI responded with a song that doesn't exist"));
                    return new Suggestion(song, prompt, rawSuggestion.reason());
                })
                .collect(Collectors.toList());

    }

}
