package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import org.junit.jupiter.api.Disabled;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @MockBean
    MemberMapper memberMapper;

    @Autowired
    ObjectMapper objectMapper;

    MemberResponse dummyResponse = MemberResponse.builder()
            .memberId(1L)
            .name("최길동")
            .birth(LocalDate.of(2000, 1, 1))
            .email("gongju@naver.com")
            .contact("010-1234-5678")
            .point(BigDecimal.ZERO)
            .status(Status.ACTIVE)
            .role(Role.USER)
            .gradeName(GradeName.GOLD.toString())
            .lastLoginAt(null)
            .build();

    @Test
    @DisplayName("회원 전체 목록 조회")
    void getAllMembers() throws Exception {
        Mockito.when(memberService.getAllMembers()).thenReturn(List.of(dummyResponse));

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(1L));
    }

    @Test
    @DisplayName("로그인 성공")
    @Disabled
    void login() throws Exception {
        MemberLoginRequest loginRequest = new MemberLoginRequest("gongju@naver.com", "$pw123456789");

        Mockito.when(memberService.login(any(MemberLoginRequest.class))).thenReturn(dummyResponse);

        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("gongju@naver.com"));

        assertEquals("gongju@naver.com", loginRequest.getEmail());
        assertEquals("$pw123456789", loginRequest.getPassword());
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

        Member dummyEntity = Member.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .birth(registerRequest.getBirth())
                .contact(registerRequest.getContact())
                .build();

        Mockito.when(memberMapper.toEntity(any(MemberRegisterRequest.class))).thenReturn(dummyEntity);
        Mockito.when(memberService.register(any(MemberRegisterRequest.class))).thenReturn(dummyResponse);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("최길동"));

        assertEquals("최길동", registerRequest.getName());
        assertEquals("gongju@naver.com", registerRequest.getEmail());
        assertEquals("$pw123456789", registerRequest.getPassword());
        assertEquals("010-1234-5678", registerRequest.getContact());
        assertEquals(LocalDate.of(2000, 1, 1), registerRequest.getBirth());
    }

    @Test
    @DisplayName("회원 단건 조회")
    void getMember() throws Exception {
        Mockito.when(memberService.getMemberById(1L)).thenReturn(dummyResponse);

        mockMvc.perform(get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L));

        assertEquals(1L, dummyResponse.getMemberId());
        assertEquals("최길동", dummyResponse.getName());
        assertEquals("gongju@naver.com", dummyResponse.getEmail());
        assertEquals("010-1234-5678", dummyResponse.getContact());
        assertEquals(GradeName.GOLD.toString(), dummyResponse.getGradeName());
        assertEquals(Role.USER, dummyResponse.getRole());
        assertEquals(Status.ACTIVE, dummyResponse.getStatus());
        assertEquals(BigDecimal.ZERO, dummyResponse.getPoint());
    }

    @Test
    @DisplayName("회원 정보 수정")
    void updateMember() throws Exception {
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .name("업데이트된 회원명")
                .contact("010-9999-8888")
                .build();

        MemberResponse updatedResponse = MemberResponse.builder()
                .memberId(1L)
                .name(updateRequest.getName())
                .contact(updateRequest.getContact())
                .email("gongju@naver.com")
                .build();

        Mockito.when(memberService.updateMember(eq(1L), any(MemberUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("업데이트된 회원명"));

        assertEquals("업데이트된 회원명", updateRequest.getName());
        assertEquals("010-9999-8888", updateRequest.getContact());
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception {
        mockMvc.perform(delete("/members/1"))
                .andExpect(status().isOk());

        Mockito.verify(memberService).removeMember(1L);
    }
}
