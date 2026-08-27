package server.shareboard.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrHandler {

    // 409 - e.g. duplicate username, or a foreign key pointing to a non-existent record
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException error) {
        return new ResponseEntity<>(
                "Data integrity has been violated, this is due to duplicate value(s) or reference to non-existing record(s)",
                HttpStatus.CONFLICT
        );
    }

    // 400 - malformed or missing JSON in the request body
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Object> handleHttpMessageConversionException(HttpMessageConversionException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 415 - wrong Content-Type header (e.g. not application/json)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 500 - catch-all safety net for anything else unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception error) {
        return new ResponseEntity<>("Sorry, server down! Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}