package com.betacom.anynoteapi.exceptions;

public class SelfPermissionAssignmentException extends RuntimeException {
    public SelfPermissionAssignmentException() {
        super("Cannot share item with its owner");
    }
}
