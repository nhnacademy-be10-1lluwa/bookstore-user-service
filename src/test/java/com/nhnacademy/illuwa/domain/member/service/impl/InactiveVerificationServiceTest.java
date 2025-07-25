package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InactiveVerificationServiceTest {

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @Mock
    MemberService memberService;

    @Mock
    MessageService messageService;

    @InjectMocks
    InactiveVerificationService inactiveVerificationService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("입력한 코드가 저장된 인증번호와 일치하면 true 반환")
    void verifyCode_shouldReturnTrue_whenCodesMatch() {
        String email = "test@example.com";
        String code = "123456";

        when(valueOperations.get("verify:" + email)).thenReturn(code);

        boolean result = inactiveVerificationService.verifyCode(email, code);

        assertTrue(result);
    }

    @Test
    @DisplayName("입력한 코드가 null이면 false 반환")
    void verifyCode_shouldReturnFalse_whenInputCodeIsNull() {
        String email = "test@example.com";

        boolean result = inactiveVerificationService.verifyCode(email, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("입력한 코드와 저장된 인증번호가 다르면 false 반환")
    void verifyCode_shouldReturnFalse_whenCodesDoNotMatch() {
        String email = "test@example.com";
        String storedCode = "123456";
        String inputCode = "654321";

        when(valueOperations.get("verify:" + email)).thenReturn(storedCode);

        boolean result = inactiveVerificationService.verifyCode(email, inputCode);

        assertFalse(result);
    }

    @Test
    @DisplayName("저장된 인증번호가 없으면 false 반환")
    void verifyCode_shouldReturnFalse_whenStoredCodeIsNull() {
        String email = "test@example.com";
        String inputCode = "123456";

        when(valueOperations.get("verify:" + email)).thenReturn(null);

        boolean result = inactiveVerificationService.verifyCode(email, inputCode);

        assertFalse(result);
    }

    @Test
    @DisplayName("인증 성공 시 회원을 재활성화하고 성공 메시지를 보냄")
    void verifyAndReactivateMember_shouldReturnTrue_whenVerificationSucceeds() {
        long memberId = 1L;
        String email = "test@example.com";
        String code = "123456";

        when(valueOperations.get("verify:" + email)).thenReturn(code);
        when(memberService.getMemberById(memberId)).thenReturn(
                MemberResponse.builder().name("카리나").build()
        );

        boolean result = inactiveVerificationService.verifyAndReactivateMember(memberId, email, code);

        assertTrue(result);
        verify(memberService).reactivateMember(memberId);
        verify(messageService).sendDoorayMessage(
                argThat(request ->
                        request.getAttachmentTitle().contains("환영합니다") &&
                                request.getAttachmentText().contains("카리나")
                )
        );
    }
}