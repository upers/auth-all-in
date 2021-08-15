package com.msavchuk.exception.auth;

public class EmailNotFoundException extends Exception {

    public EmailNotFoundException(final String message) {
        super(message);
    }

}
