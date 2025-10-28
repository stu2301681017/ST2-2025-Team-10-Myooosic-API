package io.github.stu2301681017.MyooosicAPI.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SongIdentifier(
        @NotBlank() @Size(max = 128) String name,
        @NotBlank() @Size(max = 128) String author
) {
}
