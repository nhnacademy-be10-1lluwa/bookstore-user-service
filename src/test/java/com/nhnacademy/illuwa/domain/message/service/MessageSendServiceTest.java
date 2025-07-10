package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.common.client.DoorayMessageClient;
import com.nhnacademy.illuwa.common.exception.ActionNotAllowedException;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageSendServiceTest {

    @Mock
    DoorayMessageClient doorayMessageClient;

    @Mock
    MemberService memberService;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @InjectMocks
    MessageSendService messageSendService;

    SendMessageRequest baseRequest() {
        SendMessageRequest request = new SendMessageRequest();
        request.setBotName("1lluwa");
        request.setRecipientName("공주님");
        request.setRecipientEmail("gongju@naver.com");
        return request;
    }

    @Test
    @DisplayName("두레이메시지 전송 - attachment 포함")
    void testSendDoorayMessage_withAttachment() {
        SendMessageRequest request = baseRequest();
        request.setText("내용");
        request.setAttachmentTitle("제목");
        request.setAttachmentText("본문");
        request.setAttachmentColor("blue");

        messageSendService.sendDoorayMessage(request);

        verify(doorayMessageClient).sendMessage(any(Map.class));
    }

    @Test
    @DisplayName("두레이메시지 전송 - attachment 없이")
    void testSendDoorayMessage_withoutAttachment() {
        SendMessageRequest request = baseRequest();
        request.setText("내용만 있음");

        messageSendService.sendDoorayMessage(request);

        verify(doorayMessageClient).sendMessage(any(Map.class));
    }

    @Test
    @DisplayName("두레이메시지 전송 - text 없이도 전송")
    void testSendDoorayMessage_withoutText() {
        SendMessageRequest request = baseRequest();
        request.setText(null);
        request.setAttachmentTitle("제목");
        request.setAttachmentText("내용");
        request.setAttachmentColor("red");

        messageSendService.sendDoorayMessage(request);

        verify(doorayMessageClient).sendMessage(any());
    }

    @Test
    @DisplayName("두레이메시지 전송 - 예외 발생 시 무시")
    void testSendDoorayMessage_exception() {
        SendMessageRequest request = baseRequest();
        request.setText("예외 테스트");

        doThrow(new RuntimeException("전송 실패"))
                .when(doorayMessageClient).sendMessage(any());

        assertDoesNotThrow(() -> messageSendService.sendDoorayMessage(request));
    }

    @Test
    @DisplayName("비회원 주문 메시지 전송 테스트")
    void testSendOrderMessage() {
        GuestOrderRequest orderRequest = GuestOrderRequest.builder()
                .name("비회원")
                .orderNumber("20250702091229-123456")
                .build();

        messageSendService.sendOrderMessage(orderRequest);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(doorayMessageClient).sendMessage(captor.capture());

        Map<String, Object> capturedMap = captor.getValue();
        List<Map<String, String>> attachments = (List<Map<String, String>>) capturedMap.get("attachments");

        assertEquals("1lluwa", capturedMap.get("botName"));
        assertEquals("🎁주문완료", attachments.getFirst().get("title"));
        assertTrue(attachments.getFirst().get("text").contains(orderRequest.getOrderNumber()));
    }

    @Test
    @DisplayName("인증번호 메시지 전송 성공")
    void testSendVerificationCode_success() {
        SendMessageRequest request = baseRequest();

        MemberResponse inactiveMember = MemberResponse.builder()
                .memberId(1L)
                .email("gongju@naver.com")
                .status(Status.INACTIVE)
                .build();

        when(memberService.getMemberByEmail("gongju@naver.com")).thenReturn(inactiveMember);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        messageSendService.sendVerificationCode(request);

        verify(valueOperations).set(
                startsWith("verify:"),
                matches("\\d{6}"),
                eq(3L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("인증번호 메시지 전송 실패 - 활성회원")
    void testSendVerificationCode_activeMember() {
        SendMessageRequest request = baseRequest();

        MemberResponse activeMember = MemberResponse.builder()
                .memberId(1L)
                .email("gongju@naver.com")
                .status(Status.ACTIVE)
                .build();

        when(memberService.getMemberByEmail("gongju@naver.com")).thenReturn(activeMember);

        ActionNotAllowedException ex = assertThrows(ActionNotAllowedException.class,
                () -> messageSendService.sendVerificationCode(request));

        assertEquals("휴면 회원만 인증이 필요합니다!", ex.getMessage());
    }

    @Test
    @DisplayName("인증번호 생성 형식 검증")
    void testGenerateVerificationCode_format() throws Exception {
        // reflection 제거: 대신 정적 유틸로 리팩토링 권장
        var method = MessageSendService.class.getDeclaredMethod("generateVerificationCode");
        method.setAccessible(true);

        String code = (String) method.invoke(messageSendService);

        assertTrue(code.matches("\\d{6}"));
    }
}
