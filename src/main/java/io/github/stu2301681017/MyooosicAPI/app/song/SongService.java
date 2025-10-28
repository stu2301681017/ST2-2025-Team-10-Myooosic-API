package io.github.stu2301681017.MyooosicAPI.app.song;

import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collection;

public interface SongService {
    Collection<Song> getSongsFromIdentifier(@NotNull @Valid SongIdentifier identifier);
    Collection<Song> getSongsFromString(@NotNull @Valid @NotBlank @Size(max = 1024) String search);
}
