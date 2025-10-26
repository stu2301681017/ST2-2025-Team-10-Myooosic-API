package io.github.stu2301681017.MyooosicAPI.core;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        T data,
        @NotNull HttpStatus status,
        @NotNull @Size(max = 256) String response
) {
    public boolean success() { return status.isError(); }

}
