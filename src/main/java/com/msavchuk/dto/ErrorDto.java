package com.msavchuk.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class ErrorDto {
    @Getter @Setter
    private String error;
    @Getter @Setter
    private String message;
    @Getter @Setter
    private String path;
    @Getter @Setter
    private int status;
    @Getter @Setter
    private int timestamp;

    public ErrorDto() {
    }

    public ErrorDto(String error, String message, String path, int status) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }

    public ErrorDto(HttpStatus httpStatus, String message, String path) {
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.status = httpStatus.value();
        this.timestamp = (int) (System.currentTimeMillis() / 1000);
    }
}
