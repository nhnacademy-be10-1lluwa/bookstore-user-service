package com.nhnacademy.illuwa.common.advice;

import com.nhnacademy.illuwa.common.exception.dto.ErrorResponse;
import com.nhnacademy.illuwa.domain.grade.exception.GradeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class GradeGlobalExceptionHandler {
    /*Grade 관련 예외처리*/
    @ExceptionHandler(GradeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUGradeNotFoundException(GradeNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "GRADE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}