package com.nhnacademy.illuwa.common.advice;

import com.nhnacademy.illuwa.domain.memberaddress.exception.DuplicateMemberAddressException;
import com.nhnacademy.illuwa.domain.memberaddress.exception.MemberAddressNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class AddressGlobalExceptionHandler {

    @ExceptionHandler(DuplicateMemberAddressException.class)
    public ResponseEntity<Object> handleDuplicateAddress(DuplicateMemberAddressException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        body.put("code", "DUPLICATE_ADDRESS");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MemberAddressNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(MemberAddressNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("code", "ADDRESS_NOT_FOUND");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


}
