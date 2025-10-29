package io.github.stu2301681017.MyooosicAPI.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

public record Song(
        @NotNull @JsonIgnore SongIdentifier id,
        @Positive Integer length, // in seconds
        @NotBlank @URL String url,
        @NotBlank @URL String picture,
        @NotBlank @URL String previewUrl
) {
    @JsonProperty
    public String name() {
        return id.name();
    }

    @JsonProperty
    public String author() {
        return id.author();
    }
}