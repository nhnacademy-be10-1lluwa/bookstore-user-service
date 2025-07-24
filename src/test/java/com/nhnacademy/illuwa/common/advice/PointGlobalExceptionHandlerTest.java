package com.nhnacademy.illuwa.common.advice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.nhnacademy.illuwa.common.exception.ErrorResponse;
import com.nhnacademy.illuwa.domain.point.exception.InvalidPointOperationException;
import com.nhnacademy.illuwa.domain.point.pointpolicy.exception.DuplicatePointPolicyException;
import com.nhnacademy.illuwa.domain.point.pointpolicy.exception.PointPolicyNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PointGlobalExceptionHandlerTest {

    PointGlobalExceptionHandler exceptionHandler;
    HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new PointGlobalExceptionHandler();
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/api/point-policies/sample-policy");
    }

    @Test
    void handlePolicyNotFoundException_ReturnsNotFoundResponse() {
        String errorMessage = "해당 포인트 정책을 찾을 수 없습니다: sample-policy";
        PointPolicyNotFoundException ex = new PointPolicyNotFoundException("sample-policy");

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handlePolicyNotFoundException(ex, mockRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.getError());
        assertEquals("POLICY_NOT_FOUND", errorResponse.getCode());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals("/api/point-policies/sample-policy", errorResponse.getPath());
    }

    @Test
    void handleDuplicatePolicy_ReturnsConflictResponse() {
        String errorMessage = "이미 존재하는 포인트 정책입니다: sample-policy";
        DuplicatePointPolicyException ex = new DuplicatePointPolicyException("sample-policy");

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleDuplicatePolicy(ex, mockRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), errorResponse.getError());
        assertEquals("DUPLICATE_POLICY", errorResponse.getCode());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals("/api/point-policies/sample-policy", errorResponse.getPath());
    }

    @Test
    void handleInvalidPointOperationException_ReturnsBadRequestResponse() {
        String errorMessage = "잘못된 포인트 조작입니다.";
        InvalidPointOperationException ex = new InvalidPointOperationException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleInvalidPointOperationException(ex, mockRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getError());
        assertEquals("INVALID_POINT_OPERATION", errorResponse.getCode());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals("/api/point-policies/sample-policy", errorResponse.getPath());
    }
}
