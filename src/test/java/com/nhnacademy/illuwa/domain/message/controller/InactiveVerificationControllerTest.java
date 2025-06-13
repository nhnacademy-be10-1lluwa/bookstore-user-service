package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeRequest;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.SendVerificationCodeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InactiveVerificationController.class)
class InactiveVerificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @MockBean
    SendVerificationCodeService sendCodeService;

    @MockBean
    InactiveVerificationService verificationService;

    @Test
    @DisplayName("인증번호 전송 성공")
    void sendVerificationCode_success() throws Exception {
        long memberId = 1L;
        String email = "test@example.com";

        when(memberService.getMemberById(memberId)).thenReturn(
                new com.nhnacademy.illuwa.domain.member.entity.Member() {{
                    setEmail(email);
                }}
        );

        mockMvc.perform(post("/members/{memberId}/inactive/verification-code", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").exists());

        verify(sendCodeService, times(1)).sendVerificationNumber(any(SendVerificationCodeRequest.class));
    }

    @Test
    @DisplayName("인증번호 전송 실패 시 500 반환")
    void sendVerificationCode_failure() throws Exception {
        long memberId = 1L;
        String email = "test@example.com";

        when(memberService.getMemberById(memberId)).thenReturn(new com.nhnacademy.illuwa.domain.member.entity.Member() {{
            setEmail(email);
        }});

        doThrow(new RuntimeException("fail")).when(sendCodeService).sendVerificationNumber(any());

        mockMvc.perform(post("/members/{memberId}/inactive/verification-code", memberId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("인증번호 검증 성공")
    void verifyCode_success() throws Exception {
        long memberId = 1L;
        String email = "test@example.com";
        String code = "123456";

        when(memberService.getMemberById(memberId)).thenReturn(new com.nhnacademy.illuwa.domain.member.entity.Member() {{
            setEmail(email);
        }});
        when(verificationService.verifyCode(email, code)).thenReturn(true);

        String requestBody = "{\"code\":\"" + code + "\"}";

        mockMvc.perform(post("/members/{memberId}/inactive/verification-code/verify", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").value("인증에 성공하여 휴면상태가 해제됐어요"));

        verify(memberService, times(1)).reactivateMember(memberId);
    }

    @Test
    @DisplayName("인증번호 검증 실패")
    void verifyCode_failure() throws Exception {
        long memberId = 1L;
        String email = "test@example.com";
        String code = "wrongcode";

        when(memberService.getMemberById(memberId)).thenReturn(new com.nhnacademy.illuwa.domain.member.entity.Member() {{
            setEmail(email);
        }});
        when(verificationService.verifyCode(email, code)).thenReturn(false);

        String requestBody = "{\"code\":\"" + code + "\"}";

        mockMvc.perform(post("/members/{memberId}/inactive/verification-code/verify", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").value("인증에 실패하여 휴면상태가 지속됩니다."));

        verify(memberService, never()).reactivateMember(anyLong());
    }

    @Test
    @DisplayName("멤버를 못 찾으면 MemberNotFoundException 발생")
    void memberNotFound() throws Exception {
        long memberId = 999L;

        when(memberService.getMemberById(memberId)).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(post("/members/{memberId}/inactive/verification-code", memberId))
                .andExpect(status().isNotFound());
    }
}
