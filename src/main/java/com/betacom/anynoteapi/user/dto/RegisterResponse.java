package com.betacom.anynoteapi.user.dto;

import java.time.Instant;
import java.util.UUID;

public record RegisterResponse(UUID id,
                               String login,
                               Instant createdAt) {
}
