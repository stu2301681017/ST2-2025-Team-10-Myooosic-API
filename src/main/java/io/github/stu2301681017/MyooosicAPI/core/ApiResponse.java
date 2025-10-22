package io.github.stu2301681017.MyooosicAPI.core;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        T data,
        HttpStatus status,
        String response
) {
    public boolean success() { return status.isError(); }

}
