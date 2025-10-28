package io.github.stu2301681017.MyooosicAPI.app.query;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuerySuggestionAnswer(@NotNull @Valid List<QuerySuggestion> items) {
}
