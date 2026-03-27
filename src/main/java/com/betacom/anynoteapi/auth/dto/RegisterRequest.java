package com.betacom.anynoteapi.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Size(min = 3, max = 64, message = "Login must be between 3 and 64 characters long") String login,
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
        @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
        @Pattern(regexp = ".*[@$!%*?&].*", message = "Password must contain at least one special character")
        String password) {
}
