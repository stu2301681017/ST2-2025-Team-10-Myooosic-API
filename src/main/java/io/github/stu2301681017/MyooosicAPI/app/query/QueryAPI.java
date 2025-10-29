package io.github.stu2301681017.MyooosicAPI.app.query;
import io.github.stu2301681017.MyooosicAPI.core.ApiConstraints;
import io.github.stu2301681017.MyooosicAPI.core.ApiResponse;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import io.github.stu2301681017.MyooosicAPI.core.Suggestion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
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
    public ApiResponse<Collection<Suggestion>> query(
            @PathVariable @Valid QueryPrompt prompt,
            @RequestParam(required = false) @Valid @Size(max = 20) List<SongIdentifier> avoid
    ) {
        return new ApiResponse<>(
                queryService.getSongSuggestionsForPrompt(
                        prompt,
                        9,
                        avoid != null ? avoid : List.of()
                ),
                HttpStatus.OK,
                "Successfully found 9 song suggestions");
    }

    @GetMapping("/query/single/{prompt}")
    public ApiResponse<Suggestion> querySingle(
            @PathVariable @Valid QueryPrompt prompt,
            @RequestParam(required = false) @Valid @Size(max = 20) List<SongIdentifier> avoid
    ) {
        Optional<Suggestion> first = queryService.getSongSuggestionsForPrompt(
                prompt,
                1,
                avoid != null ? avoid : List.of()
        ).stream().findFirst();
        return new ApiResponse<>(
                first.orElse(null),
                HttpStatus.OK,
                "Successfuly found 1 song suggestion"
        );
    }

}