package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.member.service.impl.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.nhnacademy.illuwa.domain.member.entity.enums.Status.INACTIVE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InactiveMemberController.class)
class InactiveMemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private InactiveVerificationService inactiveVerificationService;

    @Test
    @DisplayName("sendVerificationCode - 성공 시 메시지 발송 및 응답")
    void sendVerificationCode_Success() throws Exception{
        SendVerificationRequest request = new SendVerificationRequest("010-1234-5678");
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", "test@example.com", INACTIVE);
        SendMessageRequest messageRequest = SendMessageRequest.builder().recipientName(inactiveMember.getName()).recipientEmail(inactiveMember.getEmail()).build();
        SendMessageResponse messageResponse = new SendMessageResponse(true, inactiveMember.getEmail(), "메시지 전송이 완료되었습니다!", "인증번호를 입력해주세요!");

        when(memberService.getInactiveMemberInfoByContact(request.getContact())).thenReturn(inactiveMember);
        when(messageService.sendVerificationCode(messageRequest)).thenReturn(messageResponse);

        mockMvc.perform(post("/api/members/inactive-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(true));

    }


    @Test
    @DisplayName("receiveVerificationCode - 인증 성공 시 휴면 해제 응답")
    void receiveVerificationCode_Success() throws Exception{
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", "user@example.com", Status.INACTIVE);
        VerifyCodeRequest request = new VerifyCodeRequest("010-1234-5678", "123456");


        when(memberService.getInactiveMemberInfoByContact(request.getContact())).thenReturn(inactiveMember);
        when(inactiveVerificationService.verifyAndReactivateMember(inactiveMember.getMemberId(), inactiveMember.getEmail(), request.getCode()))
                .thenReturn(true);

        mockMvc.perform(post("/api/members/inactive-verifications/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("receiveVerificationCode - 인증 실패 시 401 반환")
    void receiveVerificationCode_Failure() throws Exception {
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", "test@email.com", Status.INACTIVE);
        VerifyCodeRequest request = new VerifyCodeRequest("010-1234-5678", "wrong-code");

        when(memberService.getInactiveMemberInfoByContact(request.getContact())).thenReturn(inactiveMember);
        when(inactiveVerificationService.verifyAndReactivateMember(inactiveMember.getMemberId(), inactiveMember.getEmail(), request.getCode())).thenReturn(false);

        mockMvc.perform(post("/api/members/inactive-verifications/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(false));

    }

    @Test
    @DisplayName("receiveVerificationCode - 회원 조회 실패 시 MemberNotFoundException 발생")
    void receiveVerificationCode_MemberNotFound() throws Exception{
        VerifyCodeRequest request = new VerifyCodeRequest("010-1234-5678","any-code");

        when(memberService.getInactiveMemberInfoByContact(request.getContact())).thenThrow(new RuntimeException("회원 없음"));

        mockMvc.perform(post("/api/members/inactive-verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }
}