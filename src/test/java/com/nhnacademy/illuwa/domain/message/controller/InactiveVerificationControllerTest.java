package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageResponse;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeRequest;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeResponse;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InactiveVerificationControllerTest {

    @InjectMocks
    private InactiveVerificationController controller;

    @Mock
    private MemberService memberService;

    @Mock
    private MessageSendService messageSendService;

    @Mock
    private InactiveVerificationService inactiveVerificationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("sendVerificationCode - 성공 시 메시지 발송 및 응답")
    void sendVerificationCode_Success() {
        long memberId = 1L;
        MemberResponse member = MemberResponse.builder()
                .name("공주")
                .email("princess@example.com")
                .build();

        when(memberService.getMemberById(memberId)).thenReturn(member);

        ResponseEntity<SendMessageResponse> response = controller.sendVerificationCode(memberId);

        verify(messageSendService).sendVerificationCode(argThat(request ->
                request.getRecipientName().equals(member.getName()) &&
                        request.getRecipientEmail().equals(member.getEmail())
        ));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting("memberId", "email", "message")
                .containsExactly(memberId, member.getEmail(), "인증번호를 발송했습니다!");
    }

    @Test
    @DisplayName("sendVerificationCode - 회원 조회 실패 시 MemberNotFoundException 발생")
    void sendVerificationCode_MemberNotFound() {
        long memberId = 99L;

        when(memberService.getMemberById(memberId)).thenThrow(new RuntimeException("회원 없음"));

        assertThatThrownBy(() -> controller.sendVerificationCode(memberId))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining(String.valueOf(memberId));
    }

    @Test
    @DisplayName("receiveVerificationCode - 인증 성공 시 휴면 해제 응답")
    void receiveVerificationCode_Success() {
        long memberId = 2L;
        String email = "user2@example.com";

        MemberResponse member = MemberResponse.builder()
                .email(email)
                .build();

        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setCode("valid-code");

        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(inactiveVerificationService.verifyAndReactivateMember(memberId, email, request.getCode()))
                .thenReturn(true);

        ResponseEntity<VerifyCodeResponse> response = controller.receiveVerificationCode(memberId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting("memberId", "email", "message")
                .containsExactly(memberId, email, "인증에 성공하여 휴면상태가 해제됐어요");
    }

    @Test
    @DisplayName("receiveVerificationCode - 인증 실패 시 401 Unauthorized 응답")
    void receiveVerificationCode_Failure() {
        long memberId = 3L;
        String email = "user3@example.com";

        MemberResponse member = MemberResponse.builder()
                .email(email)
                .build();

        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setCode("invalid-code");

        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(inactiveVerificationService.verifyAndReactivateMember(memberId, email, request.getCode()))
                .thenReturn(false);

        ResponseEntity<VerifyCodeResponse> response = controller.receiveVerificationCode(memberId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody())
                .extracting("memberId", "email", "message")
                .containsExactly(memberId, email, "인증에 실패하여 휴면상태가 지속됩니다.");
    }

    @Test
    @DisplayName("receiveVerificationCode - 회원 조회 실패 시 MemberNotFoundException 발생")
    void receiveVerificationCode_MemberNotFound() {
        long memberId = 100L;
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setCode("any-code");

        when(memberService.getMemberById(memberId)).thenThrow(new RuntimeException("회원 없음"));

        assertThatThrownBy(() -> controller.receiveVerificationCode(memberId, request))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining(String.valueOf(memberId));
    }

    @Test
    @DisplayName("getMemberOrThrow - 정상 조회 시 MemberResponse 반환")
    void getMemberOrThrow_Success() {
        long memberId = 10L;
        MemberResponse member = MemberResponse.builder().build();

        when(memberService.getMemberById(memberId)).thenReturn(member);

        MemberResponse result = controller.getMemberOrThrow(memberId);

        assertThat(result).isEqualTo(member);
    }

    @Test
    @DisplayName("getMemberOrThrow - 조회 실패 시 MemberNotFoundException 던짐")
    void getMemberOrThrow_Fail() {
        long memberId = 11L;

        when(memberService.getMemberById(memberId)).thenThrow(new RuntimeException("회원 없음"));

        assertThatThrownBy(() -> controller.getMemberOrThrow(memberId))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining(String.valueOf(memberId));
    }
}
