package com.betacom.anynoteapi.exceptions;

public class InvalidTokenTypeException extends RuntimeException {
    public InvalidTokenTypeException() {
        super("Invalid token type");
    }
}
