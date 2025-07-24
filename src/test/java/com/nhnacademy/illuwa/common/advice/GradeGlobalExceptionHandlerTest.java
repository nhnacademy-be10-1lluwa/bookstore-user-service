package com.nhnacademy.illuwa.common.advice;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nhnacademy.illuwa.common.exception.ErrorResponse;
import com.nhnacademy.illuwa.domain.grade.exception.GradeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GradeGlobalExceptionHandlerTest {

    GradeGlobalExceptionHandler exceptionHandler;
    HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GradeGlobalExceptionHandler();
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/api/grades/123");
    }

    @Test
    void handleGradeNotFoundException_ReturnsNotFoundResponse() {
        String gradeName = "GGGOLD";
        GradeNotFoundException exception = new GradeNotFoundException(gradeName);

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleUGradeNotFoundException(exception, mockRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.getError());
        assertEquals("GRADE_NOT_FOUND", errorResponse.getCode());
        assertEquals("해당 등급이 존재하지 않아요! " + gradeName, errorResponse.getMessage());
        assertEquals("/api/grades/123", errorResponse.getPath());
    }
}
