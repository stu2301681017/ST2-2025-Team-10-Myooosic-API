package io.github.stu2301681017.MyooosicAPI.app.query.persistence;

import io.github.stu2301681017.MyooosicAPI.app.auth.Registration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@Entity
public class QueryPromptEntity {

    @GeneratedValue
    @Id
    private Integer id;

    @Column(length = 256)
    private String prompt;

    @ManyToOne
    @JoinColumn()
    private Registration owner;

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL)
    private Collection<SuggestionEntity> suggestions;
}
