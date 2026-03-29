package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.exceptions.ForbiddenException;
import com.betacom.anynoteapi.item_permission.ItemPermissionRepository;
import com.betacom.anynoteapi.item_permission.ItemPermissionRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemAccessService {

    private final ItemPermissionRepository itemPermissionRepository;

    public void requireOwner(Item item, UUID userId) {
        assertHasAccess(isOwner(item, userId));
    }

    public void requireEditAccess(Item item, UUID userId) {
        assertHasAccess(isOwner(item, userId) || isEditor(item.getId(), userId));
    }

    public void requireViewAccess(Item item, UUID userId) {
        assertHasAccess(isOwner(item, userId) || hasAnyPermission(item.getId(), userId));
    }

    private void assertHasAccess(boolean hasAccess) {
        if (!hasAccess) {
            throw new ForbiddenException();
        }
    }

    private boolean isOwner(Item item, UUID userId) {
        return item.getOwner().getId().equals(userId);
    }

    private boolean isEditor(UUID itemId, UUID userId) {
        return itemPermissionRepository.existsByItemIdAndUserIdAndRoleEquals(
                itemId, userId, ItemPermissionRole.EDITOR);
    }

    private boolean hasAnyPermission(UUID itemId, UUID userId) {
        return itemPermissionRepository.existsByItemIdAndUserId(itemId, userId);
    }
}
