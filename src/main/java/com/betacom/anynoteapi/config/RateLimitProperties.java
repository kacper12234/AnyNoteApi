package com.betacom.anynoteapi.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "rate-limit")
@Validated
public record RateLimitProperties(List<@Valid Rule> rules) {
        public record Rule(
                @NotBlank String path,
                @Min(1) int capacity,
                @Min(1) int duration
        ) {}
}
