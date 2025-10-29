package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.app.song.SongIdentifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuerySuggestion(
        @NotNull() @Valid SongIdentifier identifier,
        @NotBlank() @Size(max = 128) String reason
    ) {
}
