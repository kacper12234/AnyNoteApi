package com.betacom.anynoteapi.auth.dto;

public record AuthResponse(String token,
                           long expiresIn) {
}
