package io.github.stu2301681017.MyooosicAPI.api.query;

import io.github.stu2301681017.MyooosicAPI.ai.AiService;
import io.github.stu2301681017.MyooosicAPI.ai.Prompt;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class QueryService {

    private final AiService aiService;

    public QueryService(
            AiService aiService
    ) {
        this.aiService = aiService;
    }

    public Collection<Suggestion> getSongSuggestionsForPrompt(Prompt prompt) {
        final Suggestion[] aiResponse = this.aiService.promptClass(prompt, Suggestion[].class);
        return List.of(aiResponse);
    }

}
