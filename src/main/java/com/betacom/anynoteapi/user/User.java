package com.betacom.anynoteapi.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;
    @Column(length = 64, nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
