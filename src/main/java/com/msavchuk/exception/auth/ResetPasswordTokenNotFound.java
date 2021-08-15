package com.msavchuk.exception.auth;

public class ResetPasswordTokenNotFound extends RuntimeException {

    private static final long serialVersionUID = 1261310537366287263L;

    public ResetPasswordTokenNotFound() {
        super();
    }

    public ResetPasswordTokenNotFound(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResetPasswordTokenNotFound(final String message) {
        super(message);
    }

    public ResetPasswordTokenNotFound(final Throwable cause) {
        super(cause);
    }

}