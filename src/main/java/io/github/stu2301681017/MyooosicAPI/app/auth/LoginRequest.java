package io.github.stu2301681017.MyooosicAPI.app.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max=32) String name,
        @NotBlank @Size(max=64) String password
) {
}
