package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @DisplayName("휴면 회원 정보 확인")
    void getInactiveMemberInfo() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest("sleepy@naver.com");
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", "sleepy@naver.com", INACTIVE);

        when(memberService.getInactiveMemberInfoByEmail("sleepy@naver.com"))
                .thenReturn(inactiveMember);

        mockMvc.perform(post("/api/members/inactive/check-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(3L))
                .andExpect(jsonPath("$.name").value("최길동"))
                .andExpect(jsonPath("$.email").value("sleepy@naver.com"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("check-status - 회원이 없을 때 404 반환")
    void checkStatus_MemberNotFound() throws Exception {
        String email = "notfound@example.com";
        SendVerificationRequest request = new SendVerificationRequest(email);

        when(memberService.getInactiveMemberInfoByEmail(email)).thenThrow(new RuntimeException("회원 없음"));

        mockMvc.perform(post("/api/members/inactive/check-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("sendVerificationCode - 성공 시 메시지 발송 및 응답")
    void sendVerificationCode_Success() throws Exception{
        String email = "sleepy@naver.com";
        SendVerificationRequest request = new SendVerificationRequest(email);
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", email, INACTIVE);
        SendMessageRequest messageRequest = SendMessageRequest.builder().recipientName(inactiveMember.getName()).recipientEmail(inactiveMember.getEmail()).build();
        SendMessageResponse messageResponse = new SendMessageResponse(true, email, "메시지 전송이 완료되었습니다!", "인증번호를 입력해주세요!");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(inactiveMember);
        when(messageService.sendVerificationCode(messageRequest)).thenReturn(messageResponse);

        mockMvc.perform(post("/api/members/inactive/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.email").value(email))
                    .andExpect(jsonPath("$.message").value("메시지 전송이 완료되었습니다!"))
                    .andExpect(jsonPath("$.text").value("인증번호를 입력해주세요!"));
    }


    @Test
    @DisplayName("receiveVerificationCode - 인증 성공 시 휴면 해제 응답")
    void receiveVerificationCode_Success() throws Exception{
        String email = "user@example.com";
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", email, Status.INACTIVE);
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail(email);
        request.setCode("123456");


        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(inactiveMember);
        when(inactiveVerificationService.verifyAndReactivateMember(3L, email, "123456"))
                .thenReturn(true);

        mockMvc.perform(post("/api/members/inactive/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.memberId").value(3L))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").value("인증에 성공하여 휴면상태가 해제됐어요"));
    }

    @Test
    @DisplayName("receiveVerificationCode - 인증 실패 시 401 반환")
    void receiveVerificationCode_Failure() throws Exception {
        String email = "user@example.com";
        InactiveCheckResponse inactiveMember = new InactiveCheckResponse(3L, "최길동", email, Status.INACTIVE);
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail(email);
        request.setCode("wrong-code");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenReturn(inactiveMember);
        when(inactiveVerificationService.verifyAndReactivateMember(3L, email, "wrong-code")).thenReturn(false);

        mockMvc.perform(post("/api/members/inactive/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.memberId").value(3L))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").value("인증에 실패하여 휴면상태가 지속됩니다."));
    }

    @Test
    @DisplayName("receiveVerificationCode - 회원 조회 실패 시 MemberNotFoundException 발생")
    void receiveVerificationCode_MemberNotFound() throws Exception{
        String email = "nonexistent@example.com";
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail(email);
        request.setCode("any-code");

        when(memberService.getInactiveMemberInfoByEmail(email)).thenThrow(new RuntimeException("회원 없음"));

        mockMvc.perform(post("/api/members/inactive/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }
}