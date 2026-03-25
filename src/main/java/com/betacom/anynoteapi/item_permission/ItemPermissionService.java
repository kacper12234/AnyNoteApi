package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.auth.AuthService;
import com.betacom.anynoteapi.exceptions.ForbiddenException;
import com.betacom.anynoteapi.exceptions.ItemNotFoundException;
import com.betacom.anynoteapi.exceptions.PermissionNotFoundException;
import com.betacom.anynoteapi.exceptions.UserNotFoundException;
import com.betacom.anynoteapi.item.ItemRepository;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionResponse;
import com.betacom.anynoteapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemPermissionService {

    private final ItemRepository itemRepository;
    private final ItemPermissionRepository itemPermissionRepository;
    private final ItemPermissionMapper itemPermissionMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    record PermissionGrant(boolean updatedExisting, ItemPermissionResponse permission) {
    }

    PermissionGrant shareItem(UUID itemId, ItemPermissionRequest request) {
        var user = userRepository.findById(request.userId()).orElseThrow(() -> new UserNotFoundException(request.userId()));
        var item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (!item.getOwner().getId().equals(authService.getCurrentUser().getId())) {
            throw new ForbiddenException();
        }
        var permissionOptional = itemPermissionRepository.findByItemIdAndUserId(itemId, request.userId());
        permissionOptional.ifPresent(p -> p.setRole(request.role()));
        var permission = permissionOptional.orElse(itemPermissionMapper.toItemPermission(request, item, user));
        return new PermissionGrant(
                permissionOptional.isPresent(),
                itemPermissionMapper.toPermissionResponse(itemPermissionRepository.save(permission))
        );
    }

    void deleteItemPermission(UUID itemId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        var item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (!item.getOwner().getId().equals(authService.getCurrentUser().getId())) {
            throw new ForbiddenException();
        }
        ItemPermission permission = itemPermissionRepository
                .findByItemIdAndUserId(itemId, userId)
                .orElseThrow(() -> new PermissionNotFoundException(userId, itemId));

        itemPermissionRepository.delete(permission);
    }
}
