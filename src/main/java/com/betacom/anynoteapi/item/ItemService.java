package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.audit.AuditService;
import com.betacom.anynoteapi.auth.AuthService;
import com.betacom.anynoteapi.exceptions.ForbiddenException;
import com.betacom.anynoteapi.exceptions.ItemNotFoundException;
import com.betacom.anynoteapi.exceptions.OldVersionException;
import com.betacom.anynoteapi.item.dto.*;
import com.betacom.anynoteapi.item_permission.ItemPermissionRepository;
import com.betacom.anynoteapi.item_permission.ItemPermissionRole;
import com.betacom.anynoteapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemPermissionRepository itemPermissionRepository;
    private final ItemMapper itemMapper;
    private final AuthService authService;
    private final AuditService auditService;

    CreateItemResponse createItem(CreateItemRequest request) {
        var user = authService.getCurrentUser();
        var item = itemMapper.toItem(request, user);
        var saved = itemRepository.save(item);
        return itemMapper.toCreateItemResponse(saved);
    }

    List<ItemResponse> getAllUserItems() {
        return this.itemRepository.findAllAvailableItemsForUser(authService.getCurrentUser()).stream()
                .map(itemMapper::toItemResponse)
                .toList();
    }

    UpdateItemResponse updateItem(UUID id, UpdateItemRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = authService.getCurrentUser();
        if (!item.getOwner().getId().equals(currentUser.getId()) && !isEditor(id, currentUser.getId())) {
            throw new ForbiddenException();
        }
        itemMapper.updateItem(request, item);
        try {
            return itemMapper.toUpdateResponse(itemRepository.save(item));
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new OldVersionException(itemRepository.getCurrentVersion(id));
        }
    }

    private boolean isEditor(UUID itemId, UUID userId) {
        return itemPermissionRepository.existsByItemIdAndUserIdAndRoleEquals(itemId, userId, ItemPermissionRole.EDITOR);
    }

    private boolean isShared(UUID itemId, UUID userId) {
        return itemPermissionRepository.existsByItemIdAndUserId(itemId, userId);
    }

    void deleteItem(UUID id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = authService.getCurrentUser();
        if (!item.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException();
        }
        item.markDeleted();
        itemRepository.save(item);
    }

    List<ItemHistoryResponse> getItemHistory(UUID id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = authService.getCurrentUser();
        if (!item.getOwner().getId().equals(currentUser.getId()) && !isShared(id, authService.getCurrentUser().getId())) {
            throw new ForbiddenException();
        }
        return auditService.getHistory(Item.class, id).stream().map(itemMapper::toCreateItemResponse).toList();
    }
}
