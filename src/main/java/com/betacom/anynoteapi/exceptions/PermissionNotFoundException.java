package com.betacom.anynoteapi.exceptions;

import java.util.UUID;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(UUID userId, UUID itemId) {
        super("Permission for user: " + userId + " and item: " + itemId + " not found");
    }
}
