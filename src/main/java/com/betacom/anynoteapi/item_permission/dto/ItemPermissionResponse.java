package com.betacom.anynoteapi.item_permission.dto;

import com.betacom.anynoteapi.item_permission.ItemPermissionRole;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.betacom.anynoteapi.item_permission.ItemPermission}
 */
public record ItemPermissionResponse(UUID itemId, UUID userId, ItemPermissionRole role) implements Serializable {
}