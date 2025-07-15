package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.impl.SocialMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocialMemberController.class)
class SocialMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocialMemberService socialMemberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("소셜 사용자 존재 확인 - 존재하는 경우")
    void checkSocialUser_exists() throws Exception {
        PaycoMemberRequest request = PaycoMemberRequest.builder()
                .idNo("payco-uuid-1234")
                .build();

        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .paycoId("payco-uuid-1234")
                .name("윈터")
                .email("winter@payco.com")
                .birth(LocalDate.of(2000, 1, 1))
                .contact("010-1234-5678")
                .status(Status.ACTIVE)
                .role(Role.PAYCO)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .lastLoginAt(LocalDateTime.now())
                .build();

        Mockito.when(socialMemberService.findByPaycoId("payco-uuid-1234"))
                .thenReturn(Optional.of(response));

        mockMvc.perform(post("/api/members/internal/social-members/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("윈터"));
    }

    @Test
    @DisplayName("소셜 사용자 존재 확인 - 존재하지 않음")
    void checkSocialUser_notExists() throws Exception {
        PaycoMemberRequest request = PaycoMemberRequest.builder()
                .idNo("not-found-id")
                .build();

        Mockito.when(socialMemberService.findByPaycoId("not-found-id")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/members/internal/social-members/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("소셜 사용자 등록 성공")
    void registerSocialUser_success() throws Exception {
        PaycoMemberRequest request = PaycoMemberRequest.builder()
                .idNo("payco-uuid-9999")
                .name("카리나")
                .email("karina@payco.com")
                .mobile("010-5555-1111")
                .birthdayMMdd("1124")
                .build();

        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("카리나")
                .email("karina@payco.com")
                .contact("010-5555-1111")
                .build();

        Mockito.when(socialMemberService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/members/internal/social-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("카리나"));
    }

    @Test
    @DisplayName("소셜 사용자 정보 수정 성공")
    void updateSocialUser_success() throws Exception {
        PaycoMemberUpdateRequest request = PaycoMemberUpdateRequest.builder()
                .name("지젤")
                .email("giselle@payco.com")
                .contact("010-9999-7777")
                .birth(LocalDate.of(1999, 10, 1))
                .build();

        mockMvc.perform(put("/api/members/internal/social-members")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(socialMemberService).updatePaycoMember(eq(1L), any(PaycoMemberUpdateRequest.class));
    }
}
