package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.dto.InactiveCheckResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InactiveVerificationControllerTest {

    @InjectMocks
    private InactiveVerificationController controller;

    @Mock
    private MemberService memberService;

    @Mock
    private MessageSendService messageSendService;

    @Mock
    private InactiveVerificationService inactiveVerificationService;

    @Test
    @DisplayName("sendVerificationCode - 성공 시 메시지 발송 및 응답")
    void sendVerificationCode_Success() {
        String email = "princess@example.com";
        InactiveCheckResponse member = new InactiveCheckResponse(1L, "공주", email, Status.INACTIVE);

        SendMessageResponse mockResponse = new SendMessageResponse(true, email, "전송 성공", "인증번호를 입력해주세요!");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(member);
        when(messageSendService.sendVerificationCode(any(SendMessageRequest.class))).thenReturn(mockResponse);

        SendVerificationRequest request = new SendVerificationRequest(email);
        ResponseEntity<SendMessageResponse> response = controller.sendVerificationCode(request);

        verify(messageSendService).sendVerificationCode(argThat(msg ->
                msg.getRecipientEmail().equals(email) &&
                        msg.getRecipientName().equals("공주")
        ));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting("email", "message", "text")
                .containsExactly(mockResponse.getEmail(), mockResponse.getMessage(), mockResponse.getText());
    }

    @Test
    @DisplayName("sendVerificationCode - 회원 조회 실패 시 MemberNotFoundException 발생")
    void sendVerificationCode_MemberNotFound() {
        String email = "notfound@example.com";

        when(memberService.getInactiveMemberInfoByEmail(email)).thenThrow(new RuntimeException("회원 없음"));

        assertThatThrownBy(() -> controller.sendVerificationCode(new SendVerificationRequest(email)))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("receiveVerificationCode - 인증 성공 시 휴면 해제 응답")
    void receiveVerificationCode_Success() {
        String email = "user@example.com";
        long memberId = 2L;
        InactiveCheckResponse member = new InactiveCheckResponse(memberId, "홍길동", email, Status.INACTIVE);
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail(email);
        request.setCode("123456");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(member);
        when(inactiveVerificationService.verifyAndReactivateMember(memberId, email, "123456"))
                .thenReturn(true);

        ResponseEntity<VerifyCodeResponse> response = controller.receiveVerificationCode(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting("email", "message", "success")
                .containsExactly(email, "인증에 성공하여 휴면상태가 해제됐어요", true);
    }

    @Test
    @DisplayName("receiveVerificationCode - 인증 실패 시 401 Unauthorized 응답")
    void receiveVerificationCode_Failure() {
        String email = "user2@example.com";
        long memberId = 3L;
        InactiveCheckResponse member = new InactiveCheckResponse(memberId, "홍길동", email, Status.INACTIVE);
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail(email);
        request.setCode("wrong-code");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(member);
        when(inactiveVerificationService.verifyAndReactivateMember(memberId, email, "wrong-code"))
                .thenReturn(false);

        ResponseEntity<VerifyCodeResponse> response = controller.receiveVerificationCode(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).extracting("email", "message", "success")
                .containsExactly(email, "인증에 실패하여 휴면상태가 지속됩니다.", false);
    }

    @Test
    @DisplayName("receiveVerificationCode - 회원 조회 실패 시 MemberNotFoundException 발생")
    void receiveVerificationCode_MemberNotFound() {
        String email = "nonexistent@example.com";
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail(email);
        request.setCode("any-code");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenThrow(new RuntimeException("회원 없음"));

        assertThatThrownBy(() -> controller.receiveVerificationCode(request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("getMemberOrThrow - 정상 조회 시 응답 반환")
    void getMemberOrThrow_Success() {
        String email = "active@example.com";
        InactiveCheckResponse expected = new InactiveCheckResponse(99L, "이름", email, Status.INACTIVE);

        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(expected);

        InactiveCheckResponse result = controller.getMemberOrThrow(email);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getMemberOrThrow - 조회 실패 시 MemberNotFoundException 발생")
    void getMemberOrThrow_Fail() {
        String email = "fail@example.com";

        when(memberService.getInactiveMemberInfoByEmail(email)).thenThrow(new RuntimeException("회원 없음"));

        assertThatThrownBy(() -> controller.getMemberOrThrow(email))
                .isInstanceOf(MemberNotFoundException.class);
    }
}