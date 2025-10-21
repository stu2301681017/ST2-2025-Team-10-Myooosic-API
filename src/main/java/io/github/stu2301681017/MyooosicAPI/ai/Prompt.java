package io.github.stu2301681017.MyooosicAPI.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Prompt(
        @NotBlank(message = "Prompt must not be empty")
        @Size(max = 256, message = "Prompt must be at most 256 characters")
        String prompt
) {
}
