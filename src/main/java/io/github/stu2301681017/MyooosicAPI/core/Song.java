package io.github.stu2301681017.MyooosicAPI.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

public record Song(
        @NotNull SongIdentifier id,
        @Positive Integer length, // in seconds
        @NotBlank @URL String url,
        @NotBlank @URL String previewUrl
) {
}