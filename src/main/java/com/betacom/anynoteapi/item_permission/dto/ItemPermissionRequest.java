package com.betacom.anynoteapi.item_permission.dto;

import com.betacom.anynoteapi.item_permission.ItemPermissionRole;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.betacom.anynoteapi.item_permission.ItemPermission}
 */
public record ItemPermissionRequest(UUID userId, @NotNull ItemPermissionRole role) implements Serializable {
}