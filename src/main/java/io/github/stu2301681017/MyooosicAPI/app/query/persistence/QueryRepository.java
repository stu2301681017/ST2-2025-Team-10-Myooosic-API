package io.github.stu2301681017.MyooosicAPI.app.query.persistence;

import io.github.stu2301681017.MyooosicAPI.app.auth.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface QueryRepository extends JpaRepository<QueryPromptEntity, Integer> {

    Collection<QueryPromptEntity> findByOwner_Name(String name);

}
