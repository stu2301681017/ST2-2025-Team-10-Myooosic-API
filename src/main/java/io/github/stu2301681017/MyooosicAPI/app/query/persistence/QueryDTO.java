package io.github.stu2301681017.MyooosicAPI.app.query.persistence;


import io.github.stu2301681017.MyooosicAPI.app.query.QueryPayload;
import io.github.stu2301681017.MyooosicAPI.app.query.SuggestionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public record QueryDTO(
        @NotNull @Valid QueryPayload prompt,
        @NotNull @Valid Collection<SuggestionDTO> suggestions
) {
}
