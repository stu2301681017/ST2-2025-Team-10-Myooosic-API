package io.github.stu2301681017.MyooosicAPI.ai;

import jakarta.validation.constraints.NotBlank;

public record Prompt(
        @NotBlank
        String text
) {
}
