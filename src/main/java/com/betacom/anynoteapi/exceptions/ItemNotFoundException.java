package com.betacom.anynoteapi.exceptions;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(UUID id) {
        super("Item with id: " + id + " not found");
    }
}
