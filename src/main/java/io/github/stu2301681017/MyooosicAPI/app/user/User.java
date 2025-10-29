package io.github.stu2301681017.MyooosicAPI.app.user;

import io.github.stu2301681017.MyooosicAPI.app.auth.Registration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {

    @Id
    private String name;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "name", nullable = false)
    private Registration register;
}
