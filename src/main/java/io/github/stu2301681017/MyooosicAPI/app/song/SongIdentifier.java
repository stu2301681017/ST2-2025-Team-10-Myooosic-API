package io.github.stu2301681017.MyooosicAPI.app.song;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Embeddable
public record SongIdentifier(
        @NotBlank() @Size(max = 128) String name,
        @NotBlank() @Size(max = 128) String author
) implements Serializable {
}
