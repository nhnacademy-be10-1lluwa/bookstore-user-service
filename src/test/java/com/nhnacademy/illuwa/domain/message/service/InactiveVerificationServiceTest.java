package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InactiveVerificationServiceTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @Mock
    MemberService memberService;

    @Mock
    MessageSendService messageSendService;

    @InjectMocks
    InactiveVerificationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("입력한 코드가 저장된 인증번호와 일치하면 true 반환")
    void verifyCode_shouldReturnTrue_whenCodesMatch() {
        String email = "test@example.com";
        String code = "123456";

        when(valueOperations.get("verify:" + email)).thenReturn(code);

        boolean result = service.verifyCode(email, code);

        assertTrue(result);
    }

    @Test
    @DisplayName("입력한 코드가 null이면 false 반환")
    void verifyCode_shouldReturnFalse_whenInputCodeIsNull() {
        String email = "test@example.com";

        boolean result = service.verifyCode(email, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("입력한 코드와 저장된 인증번호가 다르면 false 반환")
    void verifyCode_shouldReturnFalse_whenCodesDoNotMatch() {
        String email = "test@example.com";
        String storedCode = "123456";
        String inputCode = "654321";

        when(valueOperations.get("verify:" + email)).thenReturn(storedCode);

        boolean result = service.verifyCode(email, inputCode);

        assertFalse(result);
    }

    @Test
    @DisplayName("저장된 인증번호가 없으면 false 반환")
    void verifyCode_shouldReturnFalse_whenStoredCodeIsNull() {
        String email = "test@example.com";
        String inputCode = "123456";

        when(valueOperations.get("verify:" + email)).thenReturn(null);

        boolean result = service.verifyCode(email, inputCode);

        assertFalse(result);
    }

    @Test
    @DisplayName("인증 성공 시 회원을 재활성화하고 성공 메시지를 보냄")
    void verifyAndReactivateMember_shouldReturnTrue_whenVerificationSucceeds() {
        long memberId = 1L;
        String email = "test@example.com";
        String code = "123456";

        when(valueOperations.get("verify:" + email)).thenReturn(code);
        when(memberService.getMemberById(memberId)).thenReturn(MemberResponse.builder().name("공주님").build());

        boolean result = service.verifyAndReactivateMember(memberId, email, code);

        assertTrue(result);
        verify(memberService).reactivateMember(memberId);
        verify(messageSendService).sendDoorayMessage(any(SendMessageRequest.class));
    }

    @Test
    @DisplayName("인증 실패 시 실패 메시지를 보냄")
    void verifyAndReactivateMember_shouldReturnFalse_whenVerificationFails() {
        long memberId = 1L;
        String email = "test@example.com";
        String inputCode = "wrongCode";
        String storedCode = "correctCode";

        when(valueOperations.get("verify:" + email)).thenReturn(storedCode);
        when(memberService.getMemberById(memberId)).thenReturn(MemberResponse.builder().name("공주님").build());

        boolean result = service.verifyAndReactivateMember(memberId, email, inputCode);

        assertFalse(result);
        verify(memberService, never()).reactivateMember(anyLong());
        verify(messageSendService).sendDoorayMessage(any(SendMessageRequest.class));
    }
}
