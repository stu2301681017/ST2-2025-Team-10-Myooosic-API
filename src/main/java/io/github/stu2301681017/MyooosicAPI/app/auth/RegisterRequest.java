package io.github.stu2301681017.MyooosicAPI.app.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min=4, max=32) @Pattern(regexp = "[a-zA-Z0-9@.]+") String name,
        @NotBlank @Size(min=8, max=64) @Pattern(regexp = "\\S+") String password
) {
}
