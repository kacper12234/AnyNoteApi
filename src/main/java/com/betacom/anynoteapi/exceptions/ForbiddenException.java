package com.betacom.anynoteapi.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You don't have permission to perform this action");
    }
}
