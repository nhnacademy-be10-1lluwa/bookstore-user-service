package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.GradeService;
import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberMapper memberMapper;

    @Mock
    GradeService gradeService;

    @InjectMocks
    MemberServiceImpl memberService;

    Grade basicGrade;
    Grade goldGrade;
    Grade royalGrade;
    Grade platinumGrade;
    Member testMember;

    void setMemberId(Member member, Long memberId) {
        try {
            Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(member, memberId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        basicGrade = Grade.builder()
                .gradeName(GradeName.BASIC)
                .priority(4)
                .pointRate(new BigDecimal("0.01"))
                .minAmount(new BigDecimal(0))
                .maxAmount(new BigDecimal("100000"))
                .build();

        goldGrade = Grade.builder()
                .gradeName(GradeName.GOLD)
                .priority(3)
                .pointRate(new BigDecimal("0.02"))
                .minAmount(new BigDecimal(100000))
                .maxAmount(new BigDecimal("200000"))
                .build();

        royalGrade = Grade.builder()
                .gradeName(GradeName.ROYAL)
                .priority(2)
                .pointRate(new BigDecimal("0.025"))
                .minAmount(new BigDecimal(200000))
                .maxAmount(new BigDecimal("300000"))
                .build();

        platinumGrade = Grade.builder()
                .gradeName(GradeName.PLATINUM)
                .priority(1)
                .pointRate(new BigDecimal("0.03"))
                .minAmount(new BigDecimal(300000))
                .build();

        testMember = Member.builder()
                .name("카리나")
                .birth(LocalDate.of(2000, 4, 11))
                .email("test@example.com")
                .password("password")
                .contact("010-2345-6879")
                .grade(basicGrade)
                .netOrderAmount(BigDecimal.ZERO)
                .lastLoginAt(LocalDateTime.now().minusMonths(4))
                .grade(basicGrade)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("회원 가입 - 정상 등록 성공")
    void register_validMember_success() {
        when(memberRepository.existsByEmail(testMember.getEmail())).thenReturn(false);
        when(memberRepository.save(testMember)).thenAnswer(invocation -> {
            Member saved = invocation.getArgument(0);
            setMemberId(saved, 1L);
            return saved;
        });
        when(memberMapper.toDto(any(Member.class))).thenAnswer(invocation -> {
            Member m = invocation.getArgument(0);
            return new MemberResponse(
                    m.getMemberId(),
                    m.getName(),
                    m.getBirth(),
                    m.getEmail(),
                    m.getRole(),
                    m.getContact(),
                    m.getGrade().getGradeName().toString(),
                    m.getPoint(),
                    m.getStatus(),
                    m.getLastLoginAt()
            );
        });
        when(gradeService.getByGradeName(GradeName.BASIC)).thenReturn(basicGrade);

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

        assertThrows(InvalidInputException.class, () -> memberService.register(invalid));
    }

    @Test
    @DisplayName("회원 가입 - null 회원 정보 예외 발생")
    void register_nullMember_throwsException() {
        assertThrows(InvalidInputException.class, () -> memberService.register(null));
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
        setMemberId(testMember, 1L);

        when(memberRepository.getMemberByEmailAndPassword(testMember.getEmail(), testMember.getPassword()))
                .thenReturn(Optional.of(testMember));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        when(memberMapper.toDto(testMember)).thenReturn(
                MemberResponse.builder()
                        .memberId(testMember.getMemberId())
                        .name(testMember.getName())
                        .birth(testMember.getBirth())
                        .email(testMember.getEmail())
                        .contact(testMember.getContact())
                        .point(testMember.getPoint())
                        .role(testMember.getRole())
                        .gradeName(testMember.getGrade().toString())
                        .lastLoginAt(testMember.getLastLoginAt())
                        .build()
        );

        MemberLoginRequest request = new MemberLoginRequest(testMember.getEmail(), testMember.getPassword());
        MemberResponse result = memberService.login(request);

        assertNotNull(result.getLastLoginAt());
        assertEquals(testMember.getEmail(), result.getEmail());
    }


    @Test
    @DisplayName("로그인 - 잘못된 자격 증명 예외 발생")
    void login_invalidCredentials_throwsException() {
        MemberLoginRequest request = new MemberLoginRequest("wrong@example.com", "wrong");
        when(memberRepository.getMemberByEmailAndPassword(any(), any())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.login(request));
    }

    @Test
    @DisplayName("휴면회원 로그인")
    void login_triggersInactiveStatusChange() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(4));
        testMember.setStatus(Status.ACTIVE);
        setMemberId(testMember,1L);

        when(memberRepository.getMemberByEmailAndPassword(any(), any())).thenReturn(Optional.of(testMember));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.login(new MemberLoginRequest(testMember.getEmail(), testMember.getPassword()));

        assertEquals(Status.INACTIVE, testMember.getStatus());
    }


    @Test
    @DisplayName("회원 조회 - 존재하는 회원 성공")
    void getMemberById_exists_success() {
        setMemberId(testMember, 1L);
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
        MemberUpdateRequest updateRequest = new MemberUpdateRequest();
        updateRequest.setName("닝닝");
        updateRequest.setContact("010-9876-5432");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        when(memberMapper.updateMember(any(Member.class), any(MemberUpdateRequest.class)))
                .thenAnswer(invocation -> {
                    Member org = invocation.getArgument(0);
                    MemberUpdateRequest req = invocation.getArgument(1);
                    org.setName(req.getName());
                    org.setContact(req.getContact());
                    return org;
                });

        when(memberMapper.toDto(any(Member.class))).thenAnswer(invocation -> {
            Member m = invocation.getArgument(0);
            return MemberResponse.builder()
                    .memberId(m.getMemberId())
                    .name(m.getName())
                    .contact(m.getContact())
                    .build();
        });

        MemberResponse result = memberService.updateMember(1L, updateRequest);

        assertEquals("닝닝", result.getName());
        assertEquals("010-9876-5432", result.getContact());
    }

    @Test
    @DisplayName("회원 수정 시 이름이 정상적으로 반영되는지 확인")
    void updateMember_fieldUpdatedCorrectly() {
        setMemberId(testMember, 1L);
        MemberUpdateRequest updated = new MemberUpdateRequest();
        updated.setName("윈터");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateMember(1L, updated);

        verify(memberMapper).updateMember(testMember, updated);
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
        setMemberId(testMember, 1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        when(gradeService.calculateGrade(new BigDecimal("50000"))).thenReturn(basicGrade);

        memberService.updateMemberGrade(1L, new BigDecimal("50000"));


        assertEquals(GradeName.BASIC, testMember.getGrade().getGradeName());
    }


    @Test
    @DisplayName("순매출액 업데이트 및 등급 변경 - 골드 등급 변경")
    void updateNetOrderAmountAndChangeGrade_success() {
        setMemberId(testMember, 1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(gradeService.calculateGrade(new BigDecimal("250000"))).thenReturn(goldGrade);

        BigDecimal newAmount = new BigDecimal("250000");
        memberService.updateMemberGrade(1L, newAmount);

        assertEquals(GradeName.GOLD, testMember.getGrade().getGradeName());
    }

    @Test
    @DisplayName("순매출액 업데이트 및 등급 변경 - 플래티넘 등급 변경")
    void updateNetOrderAmountAndChangeGrade_toPlatinum() {
        setMemberId(testMember, 1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(gradeService.calculateGrade(new BigDecimal("1000000"))).thenReturn(platinumGrade);

        memberService.updateMemberGrade(1L, new BigDecimal("1000000"));

        assertEquals(GradeName.PLATINUM, testMember.getGrade().getGradeName());
    }

    @Test
    @DisplayName("회원 비활성 체크 - 마지막 로그인 3개월 전 시 상태 변경")
    void checkMemberInactive_changesToInactive() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(4));
        setMemberId(testMember, 1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.checkMemberStatus(1L);

        assertEquals(Status.INACTIVE, testMember.getStatus());
    }

    @Test
    @DisplayName("회원 비활성 체크 - 최근 로그인 시 상태 변경 없음")
    void checkMemberInactive_recentLogin_doesNotChangeStatus() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(1));
        testMember.setStatus(Status.ACTIVE);
        setMemberId(testMember, 1L);
        memberService.register(testMember);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.checkMemberStatus(1L);

        assertEquals(Status.ACTIVE, testMember.getStatus());
    }

    @Test
    @DisplayName("회원 활성화 성공")
    void reactivateMember_success() {
        testMember.setStatus(Status.INACTIVE);
        setMemberId(testMember, 1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.reactivateMember(1L);

        assertEquals(Status.ACTIVE, testMember.getStatus());
    }



    @Test
    @DisplayName("회원 삭제 - 존재하는 회원 성공")
    void removeMember_exists_success() {
        setMemberId(testMember, 1L);
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
