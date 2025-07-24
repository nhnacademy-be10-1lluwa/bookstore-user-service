package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SocialMemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberService memberService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    SocialMemberService socialMemberService;

    MemberMapper memberMapper = new MemberMapperImpl();

    void setMemberId(Member member) {
        try {
            Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(member, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        socialMemberService = new SocialMemberService(memberRepository, memberService, passwordEncoder, memberMapper);
    }

    @Test
    @DisplayName("Payco ID로 회원 조회 성공")
    void findByPaycoId_success() {
        Member member = Member.builder()
                .paycoId("payco-1234")
                .email("test@payco.com")
                .name("카리나")
                .status(Status.ACTIVE)
                .lastLoginAt(LocalDateTime.now().minusDays(1))
                .build();
        setMemberId(member);

        when(memberRepository.findByPaycoId("payco-1234")).thenReturn(Optional.of(member));
        when(memberRepository.save(any())).thenReturn(member);
        doNothing().when(memberService).updateMemberStatus(anyLong());

        Optional<MemberResponse> result = socialMemberService.findByPaycoId("payco-1234");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@payco.com");
        verify(memberService).updateMemberStatus(1L);
    }

    @Test
    @DisplayName("Payco 회원 등록 성공")
    void register_success() {
        PaycoMemberRequest request = PaycoMemberRequest.builder()
                .idNo("payco-uuid-9876")
                .name("윈터")
                .email("winter@payco.com")
                .mobile("010-1234-5678")
                .birthdayMMdd("0421")
                .build();

        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .paycoId("payco-1234")
                .email("winter@payco.com")
                .name("윈터")
                .contact("010-1234-5678")
                .role(Role.PAYCO)
                .birth(LocalDate.of(2001, 1, 1))
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .lastLoginAt(LocalDateTime.now().minusDays(1))
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(memberService.register(any())).thenReturn(response);

        MemberResponse result = socialMemberService.register(request);

        assertThat(result.getName()).isEqualTo("윈터");
        verify(memberService).register(any(MemberRegisterRequest.class));
    }

    @Test
    @DisplayName("Payco 회원 등록 - 필수값 누락 시 기본값 사용")
    void register_withMissingOptionalFields() {
        PaycoMemberRequest request = PaycoMemberRequest.builder()
                .idNo("payco-uuid-null")
                .name(null)
                .email(null)
                .mobile(null)
                .birthdayMMdd("1231")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(memberService.register(any())).thenAnswer(invocation -> {
            MemberRegisterRequest req = invocation.getArgument(0);
            return MemberResponse.builder()
                    .name(req.getName())
                    .email(req.getEmail())
                    .contact(req.getContact())
                    .birth(req.getBirth())
                    .build();
        });

        MemberResponse result = socialMemberService.register(request);

        assertThat(result.getName()).isEqualTo("페이코 유저");
        assertThat(result.getEmail()).startsWith("payco_user_");
        assertThat(result.getContact()).isEqualTo("010-0000-0000");
        assertThat(result.getBirth()).isEqualTo(LocalDate.of(1000, 12, 31));
    }

    @Test
    @DisplayName("Payco 회원 등록 - 잘못된 생일 포맷 처리")
    void register_withInvalidBirthdayFormat() {
        PaycoMemberRequest request = PaycoMemberRequest.builder()
                .idNo("payco-bad-birth")
                .birthdayMMdd("abcd") // 잘못된 포맷
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(memberService.register(any())).thenAnswer(invocation -> {
            MemberRegisterRequest req = invocation.getArgument(0);
            return MemberResponse.builder()
                    .birth(req.getBirth())
                    .build();
        });

        MemberResponse result = socialMemberService.register(request);

        assertThat(result.getBirth()).isEqualTo(LocalDate.of(1000, 1, 1)); // 기본 생일
    }

    @Test
    @DisplayName("Payco 회원 정보 수정 성공")
    void updatePaycoMember_success() {
        PaycoMemberUpdateRequest updateRequest = PaycoMemberUpdateRequest.builder()
                .name("지젤")
                .birth(LocalDate.of(1999, 10, 1))
                .email("giselle@payco.com")
                .contact("010-9999-8888")
                .build();

        Member member = Member.builder()
                .name("기존")
                .birth(LocalDate.of(1990, 1, 1))
                .email("old@payco.com")
                .contact("010-0000-0000")
                .build();
        setMemberId(member);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any())).thenReturn(member);

        socialMemberService.updatePaycoMember(1L, updateRequest);

        assertThat(member.getName()).isEqualTo("지젤");
        assertThat(member.getEmail()).isEqualTo("giselle@payco.com");
        assertThat(member.getBirth()).isEqualTo(LocalDate.of(1999, 10, 1));
        assertThat(member.getContact()).isEqualTo("010-9999-8888");
    }

    @Test
    @DisplayName("Payco 회원 정보 수정 실패 - 회원 없음")
    void updatePaycoMember_fail_memberNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        PaycoMemberUpdateRequest request = PaycoMemberUpdateRequest.builder().build();

        assertThrows(MemberNotFoundException.class,
                () -> socialMemberService.updatePaycoMember(1L, request));
    }
}