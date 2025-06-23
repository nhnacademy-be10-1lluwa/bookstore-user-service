package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.common.client.DoorayMessageClient;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageSendServiceTest {

    @InjectMocks
    MessageSendService messageSendService;

    @Mock
    DoorayMessageClient doorayMessageClient;

    @Mock
    MemberService memberService;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    SendMessageRequest baseRequest() {
        SendMessageRequest request = new SendMessageRequest();
        request.setBotName("illuwa");
        request.setRecipientName("공주님");
        request.setRecipientEmail("gongju@naver.com");
        return request;
    }

    @Test
    @DisplayName("두레이메시지전송 - attachment 첨부")
    void testSendDoorayMessage_withAttachment() {
        SendMessageRequest request = baseRequest();
        request.setText("내용입니다");
        request.setAttachmentTitle("제목");
        request.setAttachmentText("본문");
        request.setAttachmentColor("blue");

        messageSendService.sendDoorayMessage(request);

        verify(doorayMessageClient, times(1)).sendMessage(any(Map.class));
    }

    @Test
    @DisplayName("두레이메시지전송 - attachment 제외")
    void testSendDoorayMessage_withoutAttachment() {
        SendMessageRequest request = baseRequest();
        request.setText("내용만 있음");

        messageSendService.sendDoorayMessage(request);

        verify(doorayMessageClient, times(1)).sendMessage(any(Map.class));
    }

    @Test
    @DisplayName("두레이메시지전송 - text 제외")
    void testSendDoorayMessage_textIsNull() {
        SendMessageRequest request = new SendMessageRequest();
        request.setBotName("1lluwa");
        request.setText(null);
        request.setAttachmentTitle("제목");
        request.setAttachmentText("내용");
        request.setAttachmentColor("blue");

        messageSendService.sendDoorayMessage(request);

        verify(doorayMessageClient).sendMessage(any());
    }
    @Test
    @DisplayName("두레이메시지전송 - 예외발생")
    void testSendDoorayMessage_exceptionThrown() {
        SendMessageRequest request = baseRequest();
        request.setText("예외 발생할 메시지");

        doThrow(new RuntimeException("전송 실패")).when(doorayMessageClient).sendMessage(any());

        assertDoesNotThrow(() -> messageSendService.sendDoorayMessage(request));
    }

    @Test
    @DisplayName("주문메시지 전송")
    void testSendOrderMessage() {
        SendMessageRequest request = baseRequest();
        String orderNumber = "ORD123456";

        messageSendService.sendOrderMessage(request, orderNumber);

        assertTrue(request.getText().contains("주문이 완료되었습니다"));
        assertEquals("🎁주문완료", request.getAttachmentTitle());
        assertTrue(request.getAttachmentText().contains(orderNumber));
    }

    @Test
    @DisplayName("인증번호메시지 전송 - 성공")
    void testSendVerificationCode_success() {
        SendMessageRequest request = baseRequest();

        MemberResponse mockMember = MemberResponse.builder()
                .memberId(1L)
                .name("공주님")
                .email("gongju@naver.com")
                .contact("010")
                .birth(LocalDate.of(2000, 1, 1))
                .point(BigDecimal.ZERO)
                .status(Status.INACTIVE)
                .role(Role.USER)
                .gradeName("BASIC")
                .lastLoginAt(null)
                .build();

        when(memberService.getMemberByEmail("gongju@naver.com")).thenReturn(mockMember);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        messageSendService.sendVerificationCode(request);

        assertTrue(request.getAttachmentText().contains("3분 동안"));
        assertEquals("🔑인증번호", request.getAttachmentTitle());

        verify(valueOperations, times(1))
                .set(startsWith("verify:"), anyString(), eq(3L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("인증번호 메시지 - 활성회원인 경우")
    void testSendVerificationCode_statusNotInactive() {
        SendMessageRequest request = baseRequest();
        request.setRecipientEmail("gongju@naver.com");

        MemberResponse activeMember = MemberResponse.builder()
                .memberId(1L)
                .email("gongju@naver.com")
                .status(Status.ACTIVE)
                .build();

        when(memberService.getMemberByEmail("gongju@naver.com")).thenReturn(activeMember);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> messageSendService.sendVerificationCode(request));

        assertEquals("휴면 회원만 인증이 필요합니다!", ex.getMessage());
    }

    @Test
    @DisplayName("인증번호 생성 검증")
    void testGenerateVerificationCode_format() throws Exception {
        String code = MessageSendService.class
                .getDeclaredMethod("generateVerificationCode")
                .invoke(messageSendService)
                .toString();

        assertTrue(code.matches("\\d{6}"));
    }
}
