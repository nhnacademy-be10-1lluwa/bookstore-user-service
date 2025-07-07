package com.nhnacademy.illuwa.common.advice;

import com.nhnacademy.illuwa.common.exception.dto.ErrorResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.DuplicatePointPolicyException;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PointPolicyGlobalExceptionHandler {

    @ExceptionHandler(DuplicatePointPolicyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatePolicy(DuplicatePointPolicyException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "DUPLICATE_POLICY",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PointPolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFoundException(PointPolicyNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "POLICY_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
