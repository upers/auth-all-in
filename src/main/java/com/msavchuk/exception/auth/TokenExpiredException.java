package com.msavchuk.exception.auth;

public final class TokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 5861313237366287163L;

    public TokenExpiredException() {
        super();
    }

    public TokenExpiredException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TokenExpiredException(final String message) {
        super(message);
    }

    public TokenExpiredException(final Throwable cause) {
        super(cause);
    }

}
