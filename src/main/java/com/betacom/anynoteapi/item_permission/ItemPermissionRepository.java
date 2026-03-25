package com.betacom.anynoteapi.item_permission;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemPermissionRepository extends CrudRepository<ItemPermission, UUID> {

    boolean existsByItemIdAndUserIdAndRoleEquals(UUID itemId, UUID userId, ItemPermissionRole role);

    boolean existsByItemIdAndUserId(UUID itemId, UUID userId);

    Optional<ItemPermission> findByItemIdAndUserId(UUID itemId, UUID userId);
}
