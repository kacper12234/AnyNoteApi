package com.betacom.anynoteapi.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Invalid credentials");
    }
}
