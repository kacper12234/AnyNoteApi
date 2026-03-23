package com.betacom.anynoteapi.auth.dto;

import jakarta.validation.constraints.Size;

public record RegisterRequest(@Size(min = 3, max = 64) String login, @Size(min = 8) String password) {
}
