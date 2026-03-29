package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.audit.AuditService;
import com.betacom.anynoteapi.exceptions.ItemNotFoundException;
import com.betacom.anynoteapi.exceptions.WrongVersionException;
import com.betacom.anynoteapi.item.dto.*;
import com.betacom.anynoteapi.user.UserProvider;
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
    private final ItemAccessService itemAccessService;
    private final ItemMapper itemMapper;
    private final UserProvider userProvider;
    private final AuditService auditService;

    CreateItemResponse createItem(CreateItemRequest request) {
        var user = userProvider.getCurrentUser();
        var item = itemMapper.toItem(request, user);
        var saved = itemRepository.save(item);
        return itemMapper.toCreateItemResponse(saved);
    }

    List<ItemResponse> getAllUserItems() {
        return this.itemRepository.findAllAvailableItemsForUser(userProvider.getCurrentUser()).stream()
                .map(itemMapper::toItemResponse)
                .toList();
    }

    UpdateItemResponse updateItem(UUID id, UpdateItemRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = userProvider.getCurrentUser();
        itemAccessService.requireEditAccess(item, currentUser.getId());
        if (!request.version().equals(item.getVersion())) {
            throw new WrongVersionException(item.getVersion());
        }
        itemAccessService.requireEditAccess(item, currentUser.getId());
        itemMapper.patchItemFromRequest(request, item);
        try {
            return itemMapper.toUpdateResponse(itemRepository.save(item));
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new WrongVersionException(itemRepository.getCurrentVersion(id));
        }
    }

    void deleteItem(UUID id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = userProvider.getCurrentUser();
        itemAccessService.requireOwner(item, currentUser.getId());
        item.markDeleted();
        itemRepository.save(item);
    }

    List<ItemHistoryResponse> getItemHistory(UUID id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User currentUser = userProvider.getCurrentUser();
        itemAccessService.requireViewAccess(item, currentUser.getId());
        return auditService.getHistory(Item.class, id).stream().map(itemMapper::toHistoryResponse).toList();
    }

}
