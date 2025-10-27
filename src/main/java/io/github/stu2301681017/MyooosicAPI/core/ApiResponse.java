package io.github.stu2301681017.MyooosicAPI.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        T data,
        @NotNull @JsonIgnore HttpStatus status,
        @NotNull @Size(max = 256) String response
) {
    @JsonProperty() public boolean success() { return !status.isError(); }
    @JsonProperty("status")
    public int getStatusNumber() { return status.value(); }

}
