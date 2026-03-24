package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.auth.AuthService;
import com.betacom.anynoteapi.exceptions.ForbiddenException;
import com.betacom.anynoteapi.exceptions.ItemNotFoundException;
import com.betacom.anynoteapi.exceptions.OldVersionException;
import com.betacom.anynoteapi.item.dto.*;
import com.betacom.anynoteapi.item.permission.ItemPermissionRepository;
import com.betacom.anynoteapi.item.permission.ItemPermissionRole;
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

    public CreateItemResponse createItem(CreateItemRequest request) {
        var user = authService.getCurrentUser();
        var item = itemMapper.toItem(request, user);
        var saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    public List<ItemResponse> getAllUserItems() {
        return this.itemRepository.findAllAvailableItemsForUser(authService.getCurrentUser()).stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    public UpdateItemResponse updateItem(UUID id, UpdateItemRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = authService.getCurrentUser();
        if (item.getOwner().getId().equals(currentUser.getId()) || isEditor(id, currentUser.getId())) {
            itemMapper.updateItem(request, item);
            try {
                return itemMapper.toUpdateResponse(itemRepository.save(item));
            } catch (ObjectOptimisticLockingFailureException ex) {
                throw new OldVersionException(itemRepository.getCurrentVersion(id));
            }
        }
        throw new ForbiddenException();
    }

    private boolean isEditor(UUID itemId, UUID userId) {
        return itemPermissionRepository.existsByItemIdAndUserIdAndRoleEquals(itemId, userId, ItemPermissionRole.EDITOR);
    }

    public void deleteItem(UUID id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = authService.getCurrentUser();
        if (item.getOwner().getId().equals(currentUser.getId())) {
            item.markDeleted();
            itemRepository.save(item);
        } else {
            throw new ForbiddenException();
        }
    }

}
