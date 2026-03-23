package com.betacom.anynoteapi.user.dto;

public record LoginResponse(String token,
                            long expiresIn) {
}
