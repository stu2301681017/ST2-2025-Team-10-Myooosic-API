package io.github.stu2301681017.MyooosicAPI.integration.deezer;

import java.util.List;

public record DeezerResponse(
        List<Track> data
) {
}
