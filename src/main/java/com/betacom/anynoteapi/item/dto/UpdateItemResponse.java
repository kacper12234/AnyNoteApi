package com.betacom.anynoteapi.item.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.betacom.anynoteapi.item.Item}
 */
public record UpdateItemResponse(UUID id, String title, String content, Long version,
                                 Instant updatedAt) implements Serializable {
}