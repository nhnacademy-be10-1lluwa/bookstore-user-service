package com.nhnacademy.illuwa.common.advice;

import com.nhnacademy.illuwa.domain.guest.exception.GuestNotFoundException;
import com.nhnacademy.illuwa.domain.member.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.domain.member.exception.InvalidRequestException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.exception.UnauthorizedMemberAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserGlobalExceptionHandler {

    /*Member 관련 예외처리*/
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequest(InvalidRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateMemberException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFound(MemberNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedMemberAccessException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedMemberAccessException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /*guest 관련 예외처리*/
    @ExceptionHandler(GuestNotFoundException.class)
    public ResponseEntity<String> handleGuestNotFound(GuestNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }


}
