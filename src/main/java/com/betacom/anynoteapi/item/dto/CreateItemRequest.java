package com.betacom.anynoteapi.item.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link com.betacom.anynoteapi.item.Item}
 */
public record CreateItemRequest(@NotBlank String title, String content) implements Serializable {
}