package io.github.stu2301681017.MyooosicAPI.app.query;
import io.github.stu2301681017.MyooosicAPI.core.ApiConstraints;
import io.github.stu2301681017.MyooosicAPI.core.ApiResponse;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated({ApiConstraints.class})
public class QueryAPI {

    private final QueryService queryService;

    public QueryAPI(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/query/{prompt}")
    public ApiResponse<Collection<Suggestion>> query(@PathVariable @Valid QueryPrompt prompt) {
        return new ApiResponse<>(
                queryService.getSongSuggestionsForPrompt(prompt, 9),
                HttpStatus.OK,
                "Successfully found 9 song suggestions");
    }

    @GetMapping("/query/single/{query}")
    public ApiResponse<Suggestion> querySingle(@Valid @PathVariable QueryPrompt prompt) {
        Optional<Suggestion> first = queryService.getSongSuggestionsForPrompt(prompt, 1).stream().findFirst();
        return new ApiResponse<>(
                first.orElse(null),
                HttpStatus.OK,
                "Successfuly found 1 song suggestion"
        );
    }

}