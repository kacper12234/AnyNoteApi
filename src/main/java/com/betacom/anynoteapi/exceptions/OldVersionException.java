package com.betacom.anynoteapi.exceptions;

public class OldVersionException extends RuntimeException {
    public OldVersionException(Integer version) {
        super("Provided version is outdated, actual version: " + version);
    }
}
