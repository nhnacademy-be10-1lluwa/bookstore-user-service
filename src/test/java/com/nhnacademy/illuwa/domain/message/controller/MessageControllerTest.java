package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageResponse;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @InjectMocks
    private MessageController controller;

    @Mock
    private MessageSendService messageSendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("sendCustomMessage - 메시지 전송 및 성공 응답 반환")
    void sendCustomMessage_Success() {
        SendMessageRequest request = new SendMessageRequest();
        request.setRecipientName("공주");
        request.setRecipientEmail("princess@example.com");
        request.setText("테스트 메시지입니다~");

        SendMessageResponse mockResponse =
                new SendMessageResponse(true, request.getRecipientEmail(), "두레이 메시지 전송 성공!", request.getText());

        when(messageSendService.sendDoorayMessage(any(SendMessageRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<SendMessageResponse> response = controller.sendCustomMessage(request);

        verify(messageSendService, times(1)).sendDoorayMessage(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getEmail()).isEqualTo(request.getRecipientEmail());
        assertThat(response.getBody().getMessage()).isEqualTo("두레이 메시지 전송 성공!");
    }
}
