package com.betacom.anynoteapi.item.dto;

import com.betacom.anynoteapi.item.Item;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Item}
 */
public record UpdateItemRequest(String title, String content, @NotNull Integer version) implements Serializable {
}