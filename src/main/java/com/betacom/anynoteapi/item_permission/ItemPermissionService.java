package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.exceptions.*;
import com.betacom.anynoteapi.item.ItemAccessService;
import com.betacom.anynoteapi.item.ItemRepository;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionResponse;
import com.betacom.anynoteapi.user.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemPermissionService {

    private final ItemRepository itemRepository;
    private final ItemAccessService itemAccessService;
    private final ItemPermissionRepository itemPermissionRepository;
    private final ItemPermissionMapper itemPermissionMapper;
    private final UserProvider userProvider;

    record PermissionGrant(boolean updatedExisting, ItemPermissionResponse permission) {
    }

    PermissionGrant shareItem(UUID itemId, ItemPermissionRequest request) {
        var currentUserId = userProvider.getCurrentUser().getId();
        if (request.userId().equals(currentUserId)) {
            throw new SelfPermissionAssignmentException();
        }
        var item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        itemAccessService.assertOwner(item, currentUserId);
        var user = userProvider.getUser(request.userId());
        var permissionOptional = itemPermissionRepository.findByItemIdAndUserId(itemId, request.userId());
        permissionOptional.ifPresent(p -> p.setRole(request.role()));
        var permission = permissionOptional.orElse(itemPermissionMapper.toItemPermission(request, item, user));
        return new PermissionGrant(
                permissionOptional.isPresent(),
                itemPermissionMapper.toPermissionResponse(itemPermissionRepository.save(permission))
        );
    }

    void deleteItemPermission(UUID itemId, UUID userId) {
        this.userProvider.assertUserExists(userId);
        var item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        itemAccessService.assertOwner(item, userProvider.getCurrentUser().getId());
        ItemPermission permission = itemPermissionRepository
                .findByItemIdAndUserId(itemId, userId)
                .orElseThrow(() -> new PermissionNotFoundException(userId, itemId));

        itemPermissionRepository.delete(permission);
    }
}
