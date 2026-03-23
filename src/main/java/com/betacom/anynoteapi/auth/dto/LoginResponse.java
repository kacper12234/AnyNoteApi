package com.betacom.anynoteapi.auth.dto;

public record LoginResponse(String token,
                            long expiresIn) {
}
