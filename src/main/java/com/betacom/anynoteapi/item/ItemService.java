package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.audit.AuditService;
import com.betacom.anynoteapi.exceptions.ItemNotFoundException;
import com.betacom.anynoteapi.exceptions.WrongVersionException;
import com.betacom.anynoteapi.item.dto.*;
import com.betacom.anynoteapi.user.UserProvider;
import com.betacom.anynoteapi.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemAccessService itemAccessService;
    private final ItemMapper itemMapper;
    private final UserProvider userProvider;
    private final AuditService auditService;

    private final Map<UUID, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

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
            var response = itemMapper.toUpdateResponse(itemRepository.save(item));
            item.getPermissions().forEach(p -> publishUpdate(p.getUser().getId(), response));
            return response;
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new WrongVersionException(itemRepository.getCurrentVersion(id));
        }
    }

    private void publishUpdate(UUID userId, UpdateItemResponse response) {
        emitters.getOrDefault(userId, Set.of())
                .forEach(emitter -> sendUpdate(emitter, userId, response));
    }

    private void sendUpdate(SseEmitter emitter, UUID userId, UpdateItemResponse response) {
        try {
            emitter.send(SseEmitter.event().data(response));
        } catch (IOException e) {
            log.error("Error sending SSE event", e);
            remove(userId, emitter);
        }
    }

    private void remove(UUID userId, SseEmitter emitter) {
        Set<SseEmitter> set = emitters.get(userId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) {
                emitters.remove(userId);
            }
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

    public SseEmitter subscribe() {
        var userId = userProvider.getCurrentUser().getId();
        var emitter = new SseEmitter(0L);

        emitters.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(emitter);

        Runnable cleanup = () -> remove(userId, emitter);

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        return emitter;
    }

}
