package io.github.stu2301681017.MyooosicAPI.integration.deezer;

import api.deezer.DeezerApi;
import api.deezer.exceptions.DeezerException;
import api.deezer.objects.Track;
import io.github.stu2301681017.MyooosicAPI.AppProperties;
import io.github.stu2301681017.MyooosicAPI.app.song.SongService;
import io.github.stu2301681017.MyooosicAPI.core.ServiceResponseException;
import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "myooosic.songServer", havingValue = "deezer")
@Service
public class DeezerService implements SongService {

    private DeezerApi deezerApi;

    public DeezerService(
    ) {
        this.deezerApi = new DeezerApi();
    }

    public Song getSongFromIdentifier(@NotNull @Valid SongIdentifier identifier) {
        Track track;
        try {
            track = deezerApi
                    .search()
                    .searchTrack(identifier.name() + "," + identifier.author())
                    .execute()
                    .getData()
                    .stream().findFirst()
                    .orElse(null);
        } catch (DeezerException e) {
            throw new ServiceResponseException("Deezer service returned error", e);
        }
        return new Song(identifier, track.getDuration(), track.getLink(), track.getPreview());
    }

}
