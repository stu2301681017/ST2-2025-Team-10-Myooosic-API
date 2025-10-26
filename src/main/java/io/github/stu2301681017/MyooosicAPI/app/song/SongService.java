package io.github.stu2301681017.MyooosicAPI.app.song;

import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SongService {
    Song getSongFromIdentifier(@NotNull @Valid SongIdentifier identifier);
}
