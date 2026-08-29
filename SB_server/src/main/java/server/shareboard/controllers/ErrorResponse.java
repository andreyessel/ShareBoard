package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.shareboard.domain.Result;

public class ErrorResponse {

    public static ResponseEntity<Object> build(Result<?> result) {
        HttpStatus status = switch (result.getResultType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return new ResponseEntity<>(result.getErrorMessages(), status);
    }

    //this is day4
}