package io.github.stu2301681017.MyooosicAPI.app.query.persistence;

import io.github.stu2301681017.MyooosicAPI.app.song.SongIdentifier;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SuggestionEntity {

    @GeneratedValue
    @Id
    private Integer id;

    private SongIdentifier songId;

    @Column(length = 128)
    private String reason;

    @ManyToOne()
    @JoinColumn()
    private QueryPromptEntity prompt;

}
