package io.github.stu2301681017.MyooosicAPI.app.song;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class SongTools {

    private final SongService songService;

    public SongTools(
            SongService songService
    ) {
        this.songService = songService;
    }

    @Tool(description = "Verify if a song exists in the Song Service. This returns true if it EXISTS, false otherwise.")
    public boolean verifySongExistence(@ToolParam(description = "The song identifier (name + author name) to verify if it exists.") SongIdentifier identifier) {
        System.out.println("tires to verify");
        return !this.songService.getSongsFromIdentifier(identifier).isEmpty();
    }

}
