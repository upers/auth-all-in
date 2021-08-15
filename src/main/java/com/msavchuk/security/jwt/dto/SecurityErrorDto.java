package com.msavchuk.security.jwt.dto;

import org.springframework.http.HttpStatus;

public class SecurityErrorDto {
    private String error;

    private String message;

    private String path;

    private int status;

    private int timestamp;

    public SecurityErrorDto() {
    }

    public SecurityErrorDto(String error, String message, String path, int status) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }

    public SecurityErrorDto(HttpStatus httpStatus, String message, String path) {
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.status = httpStatus.value();
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
