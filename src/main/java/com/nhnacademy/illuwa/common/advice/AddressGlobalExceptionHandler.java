package com.nhnacademy.illuwa.common.advice;

import com.nhnacademy.illuwa.domain.address.exception.DuplicateAddressException;
import com.nhnacademy.illuwa.domain.address.exception.AddressNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AddressGlobalExceptionHandler {

    @ExceptionHandler(DuplicateAddressException.class)
    public ResponseEntity<String> handleAddressAlreadyExists(DuplicateAddressException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<String> handleAddressNotFound(AddressNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

}
