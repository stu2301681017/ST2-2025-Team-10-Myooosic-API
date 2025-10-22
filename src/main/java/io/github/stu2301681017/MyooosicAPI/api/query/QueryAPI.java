package io.github.stu2301681017.MyooosicAPI.api.query;
import io.github.stu2301681017.MyooosicAPI.ai.Prompt;
import io.github.stu2301681017.MyooosicAPI.core.ApiResponse;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class QueryAPI {

    private final QueryService queryService;

    public QueryAPI(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/query")
    public ApiResponse<Collection<Suggestion>> query(@Valid @RequestBody QueryPrompt query) {
        return new ApiResponse<>(
                queryService.getSongSuggestionsForPrompt(new Prompt(query.text()), 9),
                HttpStatus.OK,
                "Successfully found 9 song suggestions");
    }

    @PostMapping("/query/single")
    public ApiResponse<Suggestion> querySingle(@Valid @RequestBody QueryPrompt query) {
        Optional<Suggestion> first = queryService.getSongSuggestionsForPrompt(new Prompt(query.text()), 1).stream().findFirst();
        return new ApiResponse<>(
                first.orElse(null),
                HttpStatus.OK,
                "Successfuly found 1 song suggestion"
        );
    }

}