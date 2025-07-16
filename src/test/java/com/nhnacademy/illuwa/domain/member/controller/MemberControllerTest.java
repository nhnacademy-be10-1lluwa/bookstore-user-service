package com.nhnacademy.illuwa.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.nhnacademy.illuwa.domain.member.entity.enums.Status.INACTIVE;
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
                .email("choi@naver.com")
                .build();

        Mockito.when(memberService.getAllMembers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(1L))
                .andExpect(jsonPath("$[0].name").value("최길동"))
                .andExpect(jsonPath("$[0].email").value("choi@naver.com"));
    }

    @Test
    @DisplayName("회원가입 성공")
    void register() throws Exception {
        MemberRegisterRequest registerRequest = MemberRegisterRequest.builder()
                .name("최길동")
                .email("choi@naver.com")
                .password("$pw123456789")
                .birth(LocalDate.of(2000, 1, 1))
                .contact("010-1234-5678")
                .build();

        MemberResponse registerResponse = MemberResponse.builder()
                .memberId(1L)
                .name("최길동")
                .email("choi@naver.com")
                .build();

        Mockito.when(memberService.register(any(MemberRegisterRequest.class)))
                .thenReturn(registerResponse);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.name").value("최길동"))
                .andExpect(jsonPath("$.email").value("choi@naver.com"));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        MemberLoginRequest loginRequest = new MemberLoginRequest("choi@naver.com", "$pw123456789");

        MemberResponse loginResponse = MemberResponse.builder()
                .memberId(1L)
                .email("choi@naver.com")
                .name("카리나")
                .build();

        Mockito.when(memberService.login(any(MemberLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.email").value("choi@naver.com"))
                .andExpect(jsonPath("$.name").value("카리나"));
    }

    @Test
    @DisplayName("회원 단일 조회 - X-USER-ID 헤더")
    void getMember() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("최길동")
                .birth(LocalDate.of(2000, 1, 1))
                .email("choi@naver.com")
                .contact("010-1234-5678")
                .gradeName(GradeName.GOLD.toString())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .point(BigDecimal.ZERO)
                .build();

        Mockito.when(memberService.getMemberById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/members")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.name").value("최길동"))
                .andExpect(jsonPath("$.birth").value("2000-01-01"))
                .andExpect(jsonPath("$.email").value("choi@naver.com"))
                .andExpect(jsonPath("$.contact").value("010-1234-5678"))
                .andExpect(jsonPath("$.gradeName").value("GOLD"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.point").value(0));
    }

    @Test
    @DisplayName("회원 정보 수정 - PUT")
    void updateMember() throws Exception {
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .name("업데이트된 회원명")
                .contact("010-9999-8888")
                .build();

        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("업데이트된 회원명")
                .contact("010-9999-8888")
                .email("choi@naver.com")
                .build();

        Mockito.when(memberService.updateMember(eq(1L), any(MemberUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/members")
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("업데이트된 회원명"))
                .andExpect(jsonPath("$.contact").value("010-9999-8888"))
                .andExpect(jsonPath("$.email").value("choi@naver.com"));
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception {
        mockMvc.perform(delete("/api/members")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk());

        Mockito.verify(memberService).removeMember(1L);
    }

    @Test
    @DisplayName("이번 달 생일 회원 조회")
    void getMembersByBirthMonth() throws Exception {
        List<MemberResponse> responseList = List.of(
                MemberResponse.builder()
                        .memberId(1L)
                        .name("홍길동")
                        .birth(LocalDate.of(1990, 7, 15))
                        .email("hong@example.com")
                        .contact("010-1234-5678")
                        .gradeName(GradeName.GOLD.toString())
                        .role(Role.USER)
                        .status(Status.ACTIVE)
                        .point(BigDecimal.ZERO)
                        .build(),
                MemberResponse.builder()
                        .memberId(2L)
                        .name("이순신")
                        .birth(LocalDate.of(1985, 7, 5))
                        .email("lee@example.com")
                        .contact("010-5678-1234")
                        .gradeName(GradeName.ROYAL.toString())
                        .role(Role.USER)
                        .status(Status.ACTIVE)
                        .point(BigDecimal.valueOf(1000))
                        .build()
        );

        Mockito.when(memberService.getMembersByBirthMonth(7)).thenReturn(responseList);

        mockMvc.perform(get("/api/members/birth-month")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("홍길동"))
                .andExpect(jsonPath("$[1].name").value("이순신"));
    }

    @Test
    @DisplayName("회원 페이지 목록 조회 - gradeName 없이")
    void getPagedMembersWithoutGrade() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .name("테스터")
                .email("test@naver.com")
                .build();

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("lastLoginAt").descending());

        Mockito.when(memberService.getPagedAllMembers(pageable))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/admin/members/paged")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].memberId").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("테스터"));
    }

    @Test
    @DisplayName("회원 페이지 목록 조회 - gradeName 포함")
    void getPagedMembersWithGrade() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .memberId(2L)
                .name("그레이드유저")
                .email("grade@naver.com")
                .gradeName(GradeName.ROYAL.toString())
                .build();

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("lastLoginAt").descending());

        Mockito.when(memberService.getPagedAllMembersByGradeName(eq(GradeName.ROYAL), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/admin/members/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("grade", "ROYAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].memberId").value(2L))
                .andExpect(jsonPath("$.content[0].gradeName").value("ROYAL"));
    }

    @Test
    @DisplayName("휴면 회원 정보 확인")
    void getInactiveMemberInfo() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest("sleepy@naver.com");
        InactiveCheckResponse response = new InactiveCheckResponse(3L, "최길동", "sleepy@naver.com", INACTIVE);

        Mockito.when(memberService.getInactiveMemberInfoByEmail("sleepy@naver.com"))
                .thenReturn(response);

        mockMvc.perform(post("/api/members/check-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(3L))
                .andExpect(jsonPath("$.name").value("최길동"))
                .andExpect(jsonPath("$.email").value("sleepy@naver.com"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("비밀번호 검증 성공")
    void checkPassword() throws Exception {
        PasswordCheckRequest request = new PasswordCheckRequest("$pw123456789");

        Mockito.when(memberService.checkPassword(1L, "$pw123456789"))
                .thenReturn(true);

        mockMvc.perform(post("/api/members/check-pw")
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}