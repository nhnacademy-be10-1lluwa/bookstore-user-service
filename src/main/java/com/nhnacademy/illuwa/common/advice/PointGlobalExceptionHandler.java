package com.nhnacademy.illuwa.common.advice;

import com.nhnacademy.illuwa.common.exception.ErrorResponse;
import com.nhnacademy.illuwa.domain.point.exception.InvalidPointOperationException;
import com.nhnacademy.illuwa.domain.point.pointpolicy.exception.DuplicatePointPolicyException;
import com.nhnacademy.illuwa.domain.point.pointpolicy.exception.InactivePointPolicyException;
import com.nhnacademy.illuwa.domain.point.pointpolicy.exception.PointPolicyNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(2)
public class PointGlobalExceptionHandler {

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

    @ExceptionHandler(InactivePointPolicyException.class)
    public ResponseEntity<ErrorResponse> handleInactivePolicyException(InactivePointPolicyException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "INACTIVE_POLICY",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPointOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPointOperationException(InvalidPointOperationException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "INVALID_POINT_OPERATION",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
