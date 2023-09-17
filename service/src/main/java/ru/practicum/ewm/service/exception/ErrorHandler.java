package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnhandled(Throwable e) {
        return ApiError.builder()
                .message(e.getClass() + " - " + e.getMessage())
                .reason("Internal server error.")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        return ApiError.builder()
                .message("The required object was not found.")
                .reason(e.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException e) {
        return ApiError.builder()
                .message("Incorrectly made request.")
                .reason(e.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(DataConflictException e) {
        return ApiError.builder()
                .message("For the requested operation the conditions are not met.")
                .reason(e.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolation(ConstraintViolationException e) {
        return ApiError.builder()
                .message("Integrity constraint has been violated.")
                .reason(e.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleSQLException(SQLException e) {
        return ApiError.builder()
                .message("Integrity constraint has been violated.")
                .reason(e.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ApiError.builder()
                .message("Integrity constraint has been violated.")
                .reason(e.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
