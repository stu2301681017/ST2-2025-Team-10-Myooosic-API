package io.github.stu2301681017.MyooosicAPI.app.query.persistence;


import io.github.stu2301681017.MyooosicAPI.app.query.SuggestionPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public record QueryPayload(
        @NotNull @Valid io.github.stu2301681017.MyooosicAPI.app.query.QueryPayload prompt,
        @NotNull @Valid Collection<SuggestionPayload> suggestions
) {
}
