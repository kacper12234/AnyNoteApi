package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items/{itemId}/share")
public class ItemPermissionController {

    private final ItemPermissionService itemPermissionService;

    @PostMapping
    public ResponseEntity<ItemPermissionResponse> shareItem(@PathVariable("itemId") UUID itemId,
                                                            @RequestBody @Valid ItemPermissionRequest request) {
        var grant = itemPermissionService.shareItem(itemId, request);
        var httpStatus = grant.updatedExisting() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(httpStatus).body(grant.permission());
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deletePermission(@PathVariable("itemId") UUID itemId,
                                                 @PathVariable("userId") UUID userId) {
        itemPermissionService.deleteItemPermission(itemId, userId);
        return ResponseEntity.noContent().build();
    }

}
