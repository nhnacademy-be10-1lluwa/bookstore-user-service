package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendVerificationCodeServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    SendVerificationCodeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service.doorayWebhookUrl = "http://fake-webhook-url";
    }

    @Test
    @DisplayName("인증번호 생성 및 Redis 저장 후 두레이 메시지 전송 테스트")
    void sendVerificationNumber_shouldStoreCodeAndSendMessage() {
        SendVerificationCodeRequest request = new SendVerificationCodeRequest();
        request.setEmail("test@example.com");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(service.doorayWebhookUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        service.sendVerificationNumber(request);

        verify(valueOperations).set(startsWith("verify:"), anyString(), eq(3L), eq(TimeUnit.MINUTES));
        verify(restTemplate).exchange(
                eq(service.doorayWebhookUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );

        assertNotNull(request.getText());
        assertNotNull(request.getAttachContent());
        assertEquals("test@example.com님 🙌\n휴면해제를 위해 화면에 인증번호를 입력해주세요.", request.getText());
        assertEquals("🔑인증번호", request.getAttachContent().getKey());
        assertNotNull(request.getAttachContent().getValue());
    }

    @Test
    @DisplayName("두레이 메시지 전송 시 첨부 콘텐츠가 없을 경우 (null) 분기 테스트")
    void sendToDooray_shouldHandleNullAttachContent() {
        SendVerificationCodeRequest request = new SendVerificationCodeRequest();
        request.setEmail("testnull@example.com");
        request.setText("테스트 텍스트");
        request.setAttachContent(null);  // 첨부 콘텐츠 null

        ResponseEntity<String> responseEntity = new ResponseEntity<>("success", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(service.doorayWebhookUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        service.sendVerificationNumber(request);

        verify(restTemplate).exchange(
                eq(service.doorayWebhookUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }


    @Test
    @DisplayName("두레이 메시지 전송 실패 시 예외 처리 테스트")
    void sendToDooray_shouldHandleExceptionGracefully() {
        SendVerificationCodeRequest request = new SendVerificationCodeRequest();
        request.setEmail("fail@example.com");
        request.setText("some text");
        request.setAttachContent(null);

        doThrow(new RuntimeException("error")).when(restTemplate).exchange(
                anyString(), any(), any(), eq(String.class)
        );

        service.sendVerificationNumber(request);

        verify(restTemplate).exchange(anyString(), any(), any(), eq(String.class));
    }
}
