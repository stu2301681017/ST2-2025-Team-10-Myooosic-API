package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.app.song.Song;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SuggestionDTO(
        @NotNull Song song,
        @NotBlank() @Size(max = 256) String prompt,
        @NotBlank() @Size(max = 128) String reason
) {
}