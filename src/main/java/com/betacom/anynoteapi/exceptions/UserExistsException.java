package com.betacom.anynoteapi.exceptions;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String login) {
        super("User with login: " + login + " already exists");
    }
}
