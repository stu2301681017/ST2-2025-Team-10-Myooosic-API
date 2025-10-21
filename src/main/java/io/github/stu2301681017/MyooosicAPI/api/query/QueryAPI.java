package io.github.stu2301681017.MyooosicAPI.api.query;
import io.github.stu2301681017.MyooosicAPI.ai.Prompt;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class QueryAPI {

    private final QueryService queryService;

    public QueryAPI(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/query")
    public Collection<Suggestion> query(@Valid @RequestBody Prompt prompt) {
        return queryService.getSongSuggestionsForPrompt(prompt);
    }

}