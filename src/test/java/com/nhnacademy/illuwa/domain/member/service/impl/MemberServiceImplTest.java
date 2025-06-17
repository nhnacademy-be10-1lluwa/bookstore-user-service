package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.domain.member.exception.InvalidRequestException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberServiceImpl 단위 테스트")

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .name("카리나")
                .birth(LocalDate.of(2000, 4, 11))
                .email("test@example.com")
                .password("password")
                .contact("010-2345-6879")
                .netOrderAmount(BigDecimal.ZERO)
                .lastLoginAt(LocalDateTime.now().minusMonths(4))
                .grade(Grade.일반)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("회원 가입 - 정상 등록 성공")
    void register_validMember_success() {
        when(memberRepository.existsByEmail(testMember.getEmail())).thenReturn(false);
        when(memberRepository.save(testMember)).thenAnswer(invocation -> {
            Member saved = invocation.getArgument(0);
            saved.setMemberId(1L);
            return saved;
        });

        MemberResponse result = memberService.register(testMember);

        assertEquals(1L, result.getMemberId());
    }

    @ParameterizedTest
    @DisplayName("회원가입 실패 - 필수 필드 누락")
    @NullAndEmptySource
    void register_missingFields_throwsException(String blank) {
        Member invalid = Member.builder()
                .email(blank)
                .password("pass")
                .name("name")
                .birth(LocalDate.now())
                .contact("010-1234-5678")
                .build();

        assertThrows(InvalidRequestException.class, () -> memberService.register(invalid));
    }


    @Test
    @DisplayName("회원 가입 - null 회원 정보 예외 발생")
    void register_nullMember_throwsException() {
        assertThrows(InvalidRequestException.class, () -> memberService.register(null));
    }

    @Test
    @DisplayName("회원 가입 - 중복 이메일 예외 발생")
    void register_duplicateMember_throwsException() {
        when(memberRepository.existsByEmail(testMember.getEmail())).thenReturn(true);

        assertThrows(DuplicateMemberException.class, () -> memberService.register(testMember));
    }

    @Test
    @DisplayName("로그인 - 올바른 자격 증명 성공")
    void login_validCredentials_success() {
        testMember.setMemberId(1L);

        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member m = invocation.getArgument(0);
            m.setMemberId(1L);
            return m;
        });

        MemberResponse registeredDto = memberService.register(testMember);
        Member registered = memberRepository.findById(registeredDto.getMemberId()).get();

        MemberLoginRequest request = new MemberLoginRequest(registered.getEmail(), registered.getPassword());
        when(memberRepository.getMemberByEmailAndPassword(registered.getEmail(), registered.getPassword()))
                .thenReturn(registered);
        when(memberRepository.findById(registered.getMemberId())).thenReturn(Optional.of(registered));

        MemberResponse result = memberService.login(request);

        assertNotNull(result.getLastLoginAt());
        assertEquals(registered.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("로그인 - 잘못된 자격 증명 예외 발생")
    void login_invalidCredentials_throwsException() {
        MemberLoginRequest request = new MemberLoginRequest("wrong@example.com", "wrong");
        when(memberRepository.getMemberByEmailAndPassword(any(), any())).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> memberService.login(request));
    }

    @Test
    @DisplayName("휴면회원 로그인")
    void login_triggersInactiveStatusChange() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(4));
        testMember.setStatus(Status.ACTIVE);
        testMember.setMemberId(1L);

        when(memberRepository.getMemberByEmailAndPassword(any(), any())).thenReturn(testMember);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.login(new MemberLoginRequest(testMember.getEmail(), testMember.getPassword()));

        assertEquals(Status.INACTIVE, testMember.getStatus());
    }


    @Test
    @DisplayName("회원 조회 - 존재하는 회원 성공")
    void getMemberById_exists_success() {
        testMember.setMemberId(1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        MemberResponse result = memberService.getMemberById(1L);

        assertEquals(memberMapper.toDto(testMember), result);
    }

    @Test
    @DisplayName("회원 조회 - 존재하지 않는 회원 예외 발생")
    void getMemberById_notExists_throwsException() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(99L));
    }

    @Test
    @DisplayName("회원 수정 - 정상 수정 성공")
    void updateMember_validMember_success() {
        testMember.setMemberId(1L);
        memberService.register(testMember);

        MemberUpdateRequest updated = new MemberUpdateRequest();
        updated.setEmail("updated@example.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateMember(1L, updated);

        verify(memberMapper).updateMember(testMember, updated);
    }

    @Test
    @DisplayName("회원 수정 시 이름이 정상적으로 반영되는지 확인")
    void updateMember_fieldUpdatedCorrectly() {
        testMember.setMemberId(1L);
        MemberUpdateRequest updated = new MemberUpdateRequest();
        updated.setName("윈터");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateMember(1L, updated);

        verify(memberMapper).updateMember(testMember, updated);
        // memberMapper가 통합 테스트에서 필드 검증
    }


    @Test
    @DisplayName("회원 수정 - 존재하지 않는 회원 예외 발생")
    void updateMember_memberNotFound_throwsException() {
        MemberUpdateRequest updated = new MemberUpdateRequest();
        updated.setName("수정이름");

        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(999L, updated));
    }

    @Test
    @DisplayName("순매출액 적을 경우 일반 등급 유지")
    void updateNetOrderAmountAndChangeGrade_remainNormal() {
        testMember.setMemberId(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateNetOrderAmountAndChangeGrade(1L, new BigDecimal("50000"));

        assertEquals(Grade.일반, testMember.getGrade());
    }


    @Test
    @DisplayName("순매출액 업데이트 및 등급 변경 - 골드 등급 변경")
    void updateNetOrderAmountAndChangeGrade_success() {
        testMember.setMemberId(1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        BigDecimal newAmount = new BigDecimal("250000");
        memberService.updateNetOrderAmountAndChangeGrade(1L, newAmount);

        assertEquals(Grade.골드, testMember.getGrade());
    }

    @Test
    @DisplayName("순매출액 업데이트 및 등급 변경 - 플래티넘 등급 변경")
    void updateNetOrderAmountAndChangeGrade_toPlatinum() {
        testMember.setMemberId(1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateNetOrderAmountAndChangeGrade(1L, new BigDecimal("1000000"));

        assertEquals(Grade.플래티넘, testMember.getGrade());
    }

    @Test
    @DisplayName("회원 비활성 체크 - 마지막 로그인 3개월 전 시 상태 변경")
    void checkMemberInactive_changesToInactive() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(4));
        testMember.setMemberId(1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.checkMemberInactive(1L);

        assertEquals(Status.INACTIVE, testMember.getStatus());
    }

    @Test
    @DisplayName("회원 비활성 체크 - 최근 로그인 시 상태 변경 없음")
    void checkMemberInactive_recentLogin_doesNotChangeStatus() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(1));
        testMember.setStatus(Status.ACTIVE);
        testMember.setMemberId(1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.checkMemberInactive(1L);

        assertEquals(Status.ACTIVE, testMember.getStatus());
    }

    @Test
    @DisplayName("회원 활성화 성공")
    void reactivateMember_success() {
        testMember.setStatus(Status.INACTIVE);
        testMember.setMemberId(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.reactivateMember(1L);

        assertEquals(Status.ACTIVE, testMember.getStatus());
    }



    @Test
    @DisplayName("회원 삭제 - 존재하는 회원 성공")
    void removeMember_exists_success() {
        testMember.setMemberId(1L);
        memberService.register(testMember);

        when(memberRepository.existsById(1L)).thenReturn(true);

        memberService.removeMember(1L);

        verify(memberRepository).deleteById(1L);
    }

    @Test
    @DisplayName("회원 삭제 - 존재하지 않는 회원 예외 발생")
    void removeMember_notExists_throwsException() {
        when(memberRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(MemberNotFoundException.class, () -> memberService.removeMember(99L));
    }
}
