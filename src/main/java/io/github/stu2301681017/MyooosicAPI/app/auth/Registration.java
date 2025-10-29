package io.github.stu2301681017.MyooosicAPI.app.auth;

import io.github.stu2301681017.MyooosicAPI.app.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Registration {

    @Id
    @Column(nullable = false, length = 32)
    private String name;
    @Column(nullable = false, length = 44)
    private String passwordSha256Base64;
    @Column(nullable = false, length = 24)
    private String passwordSaltBase64;

    @OneToOne(mappedBy = "register", cascade = CascadeType.ALL)
    private User user;

}
