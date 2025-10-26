package io.github.stu2301681017.MyooosicAPI.core;

import io.github.stu2301681017.MyooosicAPI.app.query.QueryPrompt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Suggestion(
        @NotNull Song song,
        @NotNull QueryPrompt prompt,
        @NotBlank() @Size(max = 128) String reason
) {
}