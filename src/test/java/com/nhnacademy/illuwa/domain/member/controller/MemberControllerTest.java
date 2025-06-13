package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("회원가입 성공 테스트")
    void register_success() throws Exception {
        // 요청 DTO 준비
        MemberRegisterRequest request = new MemberRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("카리나");

        Member memberEntity = new Member();
        memberEntity.setEmail(request.getEmail());
        memberEntity.setPassword(request.getPassword());
        memberEntity.setName(request.getName());

        when(memberMapper.toEntity(any(MemberRegisterRequest.class))).thenReturn(memberEntity);

        when(memberService.register(any(Member.class))).thenReturn(memberEntity);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("카리나"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다!!"));
    }
}
