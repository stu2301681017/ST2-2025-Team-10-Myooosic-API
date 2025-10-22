package io.github.stu2301681017.MyooosicAPI.api.query;

import io.github.stu2301681017.MyooosicAPI.ai.AiService;
import io.github.stu2301681017.MyooosicAPI.ai.BadAIResponseException;
import io.github.stu2301681017.MyooosicAPI.ai.Prompt;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class QueryService {

    private final AiService aiService;
    private final String QUERYHEAD = """
            
            Find me {amount} songs that fit the following keywords:
            
            """;

    public QueryService(
            AiService aiService
    ) {
        this.aiService = aiService;
    }

    public Collection<Suggestion> getSongSuggestionsForPrompt(@NotNull Prompt prompt, @Positive int amount) {
        Prompt query = new Prompt(QUERYHEAD.replaceAll("\\{amount}", amount+"") + prompt.text());
        final Suggestion[] aiResponse = this.aiService.promptClass(query, Suggestion[].class);
        if (aiResponse.length < amount) {
            throw new BadAIResponseException("LLM returned less than desired suggestions: " + aiResponse.length + " < " + amount);
        }
        return List.of(aiResponse);
    }

}
