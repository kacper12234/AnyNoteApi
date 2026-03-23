package com.betacom.anynoteapi.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
}
