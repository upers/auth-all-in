package com.msavchuk.validation;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.msavchuk.dto.ErrorDto;
import com.msavchuk.exception.auth.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailNotFoundException.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, EmailNotFoundException e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, UserNotFoundException e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({ClientUrlNotFoundException.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, ClientUrlNotFoundException e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, UsernameNotFoundException e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({VerificationTokenNotFound.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, VerificationTokenNotFound e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({ResetPasswordTokenNotFound.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, ResetPasswordTokenNotFound e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, UserAlreadyExistException e) {
        return handleInternally(request, HttpStatus.CONFLICT, e);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, AccessDeniedException e) {
        return handleInternally(request, HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, InvalidTokenException e) {
        return handleInternally(request, HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, TokenExpiredException e) {
        return handleInternally(request, HttpStatus.GONE, e);
    }

    @ExceptionHandler(InvalidOldPasswordException.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, InvalidOldPasswordException e) {
        return handleInternally(request, HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, ValidationException e) {
        return handleInternally(request, HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors()
                          .stream()
                          .map(ObjectError::getDefaultMessage)
                          .collect(Collectors.joining(", "));

        return handleInternally(request, HttpStatus.BAD_REQUEST, e, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handle(HttpServletRequest request, Exception e) {
        return handleInternally(request, HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    protected ResponseEntity<ErrorDto> handleInternally(HttpServletRequest request,
                                                        HttpStatus httpStatus,
                                                        Exception e) {
        return handleInternally(request, httpStatus, e, e.getMessage());
    }

    protected ResponseEntity<ErrorDto> handleInternally(HttpServletRequest request,
                                                        HttpStatus httpStatus,
                                                        Exception e,
                                                        String msg) {
        log.debug(e.getMessage(), e);
        String path = request.getRequestURI();
        ErrorDto errorDto = new ErrorDto(httpStatus, msg, path);

        return ResponseEntity.status(httpStatus).body(errorDto);
    }
}
