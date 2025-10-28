package io.github.stu2301681017.MyooosicAPI.app.song;

import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SongTools {

    private final SongService songService;

    public SongTools(
            SongService songService
    ) {
        this.songService = songService;
    }

    @Tool(description = "Search for songs based on a string.")
    Collection<Song> searchInSongService(@ToolParam(description = "A string to search Songs by. Titles and authors are most effective.") String search) {
        return this.songService.getSongsFromString(search);
    }

    @Tool(description = "Verify if a song exists in the Song Service. This returns true if it EXISTS, false otherwise.")
    boolean verifySongExistence(@ToolParam(description = "The song identifier (name + author name) to verify if it exists.") SongIdentifier identifier) {
        return !this.songService.getSongsFromIdentifier(identifier).isEmpty();
    }

}
