package com.betacom.anynoteapi.user;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(length = 64, nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(name = "created_at")
    private Instant createdAt;
}
