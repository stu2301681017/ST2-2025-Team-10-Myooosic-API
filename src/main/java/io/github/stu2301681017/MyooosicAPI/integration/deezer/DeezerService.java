package io.github.stu2301681017.MyooosicAPI.integration.deezer;

import io.github.stu2301681017.MyooosicAPI.app.song.SongService;
import io.github.stu2301681017.MyooosicAPI.core.ServiceResponseException;
import io.github.stu2301681017.MyooosicAPI.core.Song;
import io.github.stu2301681017.MyooosicAPI.core.SongIdentifier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Map;

@ConditionalOnProperty(name = "myooosic.songServer", havingValue = "deezer")
@Service
public class DeezerService implements SongService {

    private final WebClient webClient;

    public DeezerService(
    ) {
        this.webClient = WebClient
                .builder()
                .baseUrl("https://api.deezer.com/")
                .build();
    }

    public Collection<Song> getSongsFromString(@NotNull @Valid @NotBlank @Size(max = 1024) String search) {
        try {
            DeezerResponse response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", search)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(DeezerResponse.class)
                    .block();

            return response.data()
                    .stream()
                    .map(track -> new Song(
                            new SongIdentifier(track.title(), track.artist().name()),
                            Integer.valueOf(track.duration()),
                            track.link(),
                            track.preview()
                    ))
                    .toList();

        } catch (RuntimeException e) {
            throw new ServiceResponseException("Deezer returned bad response", e);
        }
    }

    public Collection<Song> getSongsFromIdentifier(@NotNull @Valid SongIdentifier identifier) {
        return getSongsFromString(identifier.name() + ", by " + identifier.author());
    }

}
