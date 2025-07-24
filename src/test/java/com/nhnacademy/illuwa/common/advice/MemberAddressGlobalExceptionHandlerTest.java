package com.nhnacademy.illuwa.common.advice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.nhnacademy.illuwa.common.exception.ErrorResponse;
import com.nhnacademy.illuwa.domain.memberaddress.exception.MemberAddressNotFoundException;
import com.nhnacademy.illuwa.domain.memberaddress.exception.TooManyMemberAddressException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class MemberAddressGlobalExceptionHandlerTest {

    MemberAddressGlobalExceptionHandler exceptionHandler;
    HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new MemberAddressGlobalExceptionHandler();
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/api/member-addresses/42");
    }

    @Test
    void handleAddressNotFoundException_ReturnsNotFoundResponse() {
        Long addressId = 1L;
        MemberAddressNotFoundException ex = new MemberAddressNotFoundException(addressId);

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleAddressNotFoundException(ex, mockRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.getError());
        assertEquals("ADDRESS_NOT_FOUND", errorResponse.getCode());
        assertEquals("해당 주소를 찾을 수 없습니다: " + addressId, errorResponse.getMessage());
        assertEquals("/api/member-addresses/42", errorResponse.getPath());
    }

    @Test
    void handleTooManyAddressException_ReturnsBadRequestResponse() {
        String errorMessage = "주소는 10개까지 등록이 가능합니다.";
        TooManyMemberAddressException ex = new TooManyMemberAddressException();

        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleTooManyAddressException(ex, mockRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getError());
        assertEquals("TOO_MANY_ADDRESS", errorResponse.getCode());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals("/api/member-addresses/42", errorResponse.getPath());
    }
}
