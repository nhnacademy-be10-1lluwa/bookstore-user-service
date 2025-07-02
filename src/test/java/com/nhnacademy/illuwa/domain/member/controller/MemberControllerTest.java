package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 전체 목록 조회 - 관리자용")
    void getAllMembers() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("최길동")
                .email("gongju@naver.com")
                .build();

        Mockito.when(memberService.getAllMembers()).thenReturn(List.of(response));

        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(1L))
                .andExpect(jsonPath("$[0].name").value("최길동"))
                .andExpect(jsonPath("$[0].email").value("gongju@naver.com"));
    }

    @Test
    @DisplayName("회원가입 성공")
    void register() throws Exception {
        MemberRegisterRequest registerRequest = MemberRegisterRequest.builder()
                .name("최길동")
                .email("gongju@naver.com")
                .password("$pw123456789")
                .birth(LocalDate.of(2000, 1, 1))
                .contact("010-1234-5678")
                .build();

        MemberResponse registerResponse = MemberResponse.builder()
                .memberId(1L)
                .name("최길동")
                .email("gongju@naver.com")
                .build();

        Mockito.when(memberService.register(any(MemberRegisterRequest.class)))
                .thenReturn(registerResponse);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.name").value("최길동"))
                .andExpect(jsonPath("$.email").value("gongju@naver.com"));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        MemberLoginRequest loginRequest = new MemberLoginRequest("gongju@naver.com", "$pw123456789");

        MemberResponse loginResponse = MemberResponse.builder()
                .memberId(1L)
                .email("gongju@naver.com")
                .name("공주님")
                .build();

        Mockito.when(memberService.login(any(MemberLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.email").value("gongju@naver.com"))
                .andExpect(jsonPath("$.name").value("공주님"));
    }

    @Test
    @DisplayName("회원 단건 조회 - X-USER_ID 헤더")
    void getMember() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("최길동")
                .email("gongju@naver.com")
                .contact("010-1234-5678")
                .gradeName(GradeName.GOLD.toString())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .point(BigDecimal.ZERO)
                .build();

        Mockito.when(memberService.getMemberById(1L)).thenReturn(response);

        mockMvc.perform(get("/members")
                        .header("X-USER_ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.name").value("최길동"))
                .andExpect(jsonPath("$.email").value("gongju@naver.com"))
                .andExpect(jsonPath("$.contact").value("010-1234-5678"))
                .andExpect(jsonPath("$.gradeName").value("GOLD"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.point").value(0));
    }

    @Test
    @DisplayName("회원 정보 수정 - PATCH")
    void updateMember() throws Exception {
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .name("업데이트된 회원명")
                .contact("010-9999-8888")
                .build();

        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("업데이트된 회원명")
                .contact("010-9999-8888")
                .email("gongju@naver.com")
                .build();

        Mockito.when(memberService.updateMember(eq(1L), any(MemberUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/members")
                        .header("X-USER_ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("업데이트된 회원명"))
                .andExpect(jsonPath("$.contact").value("010-9999-8888"))
                .andExpect(jsonPath("$.email").value("gongju@naver.com"));
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception {
        mockMvc.perform(delete("/members")
                        .header("X-USER_ID", 1L))
                .andExpect(status().isOk());

        Mockito.verify(memberService).removeMember(1L);
    }
}
