package com.msavchuk.exception.auth;

public final class ClientUrlNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public ClientUrlNotFoundException() {
        super();
    }

    public ClientUrlNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ClientUrlNotFoundException(final String message) {
        super(message);
    }

    public ClientUrlNotFoundException(final Throwable cause) {
        super(cause);
    }

}
