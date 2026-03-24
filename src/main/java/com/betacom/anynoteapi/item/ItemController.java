package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.item.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<CreateItemResponse> createItem(@RequestBody @Valid CreateItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(request));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllUserItems() {
        return ResponseEntity.ok(itemService.getAllUserItems());
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<UpdateItemResponse> updateItem(@RequestBody @Valid UpdateItemRequest request,
                                                         @PathVariable UUID id) {
        return ResponseEntity.ok(itemService.updateItem(id, request));
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "{id}/history")
    public ResponseEntity<List<ItemHistoryResponse>> getItemHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(itemService.getItemHistory(id));
    }
}
