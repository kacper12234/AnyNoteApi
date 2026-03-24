package com.betacom.anynoteapi.item.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.betacom.anynoteapi.item.Item}
 */
public record CreateItemResponse(UUID id, UUID ownerId, String title, String content, Instant createdAt,
                                 Instant updatedAt) implements Serializable {
}