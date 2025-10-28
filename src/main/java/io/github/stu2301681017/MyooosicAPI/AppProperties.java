package io.github.stu2301681017.MyooosicAPI;

import io.github.stu2301681017.MyooosicAPI.app.song.SongServer;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix = "myooosic")
public class AppProperties {

    @NotNull()
    private SongServer songServer;


}
