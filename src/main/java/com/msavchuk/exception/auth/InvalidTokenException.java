package com.msavchuk.exception.auth;

public final class InvalidTokenException extends RuntimeException {

    private static final long serialVersionUID = 5861310537323487163L;

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(final String message) {
        super(message);
    }

    public InvalidTokenException(final Throwable cause) {
        super(cause);
    }

}
