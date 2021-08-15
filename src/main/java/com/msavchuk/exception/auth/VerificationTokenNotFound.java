package com.msavchuk.exception.auth;

public class VerificationTokenNotFound extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287263L;

    public VerificationTokenNotFound() {
        super();
    }

    public VerificationTokenNotFound(final String message, final Throwable cause) {
        super(message, cause);
    }

    public VerificationTokenNotFound(final String message) {
        super(message);
    }

    public VerificationTokenNotFound(final Throwable cause) {
        super(cause);
    }

}