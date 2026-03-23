package com.betacom.anynoteapi.user.dto;

import jakarta.validation.constraints.Size;

public record RegisterRequest(@Size(min = 3, max = 64) String username,@Size(min = 8) String password) {
}
