package io.github.stu2301681017.MyooosicAPI.app.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, String> {

    public Registration findByName(String name);

}
