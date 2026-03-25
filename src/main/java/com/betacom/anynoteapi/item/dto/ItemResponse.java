package com.betacom.anynoteapi.item.dto;

import com.betacom.anynoteapi.item_permission.ItemPermissionRole;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.betacom.anynoteapi.item.Item}
 */
public record ItemResponse(UUID id, UUID ownerId, String title, String content, Long version,
                           ItemPermissionRole myRole, Instant updatedAt) implements Serializable {
}