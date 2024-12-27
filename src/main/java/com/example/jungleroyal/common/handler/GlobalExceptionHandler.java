package com.example.jungleroyal.common.handler;

import com.example.jungleroyal.common.exceptions.DuplicateRoomException;
import com.example.jungleroyal.common.exceptions.RoomNotFoundException;
import com.example.jungleroyal.domain.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateRoomException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateRoomException(DuplicateRoomException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("DUPLICATE_ROOM_HOST_ID", e.getMessage()));
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFoundException(RoomNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ROOM_NOT_FOUND", e.getMessage()));
    }

}
