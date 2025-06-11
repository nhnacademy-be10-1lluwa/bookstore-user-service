package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.MemberAuthenticationFailedException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.exception.MemberRegistrationException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
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
        MockitoAnnotations.openMocks(this);
        testMember = new Member();
        testMember.setMemberId(1L);
        testMember.setEmail("test@example.com");
        testMember.setPassword("password");
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(4));
        testMember.setGrade(Grade.일반);
        testMember.setStatus(Status.ACTIVE);
    }

    @Test
    void register_validMember_success() {
        when(memberRepository.save(testMember)).thenReturn(testMember);
        Member result = memberService.register(testMember);
        assertEquals(testMember, result);
    }

    @Test
    void register_nullMember_throwsException() {
        assertThrows(MemberRegistrationException.class, () -> memberService.register(null));
    }

    @Test
    void login_validCredentials_success() {
        MemberLoginRequest request = new MemberLoginRequest("test@example.com", "password");
        when(memberRepository.getMemberByEmailAndPassword("test@example.com", "password"))
                .thenReturn(testMember);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        Member result = memberService.login(request);

        assertNotNull(result.getLastLoginAt());
        assertEquals(testMember.getEmail(), result.getEmail());
    }

    @Test
    void login_invalidCredentials_throwsException() {
        MemberLoginRequest request = new MemberLoginRequest("wrong@example.com", "wrong");
        when(memberRepository.getMemberByEmailAndPassword(any(), any())).thenReturn(null);
        assertThrows(MemberAuthenticationFailedException.class, () -> memberService.login(request));
    }

    @Test
    void getMemberById_exists_success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        Member result = memberService.getMemberById(1L);
        assertEquals(testMember, result);
    }

    @Test
    void getMemberById_notExists_throwsException() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(99L));
    }

    @Test
    void updateMember_validMember_success() {
        Member updated = new Member();
        updated.setMemberId(1L);
        updated.setEmail("updated@example.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateMember(updated);
        verify(memberMapper).updateMember(testMember, updated);
    }

    @Test
    void updateMember_memberNotFound_throwsException() {
        Member updated = new Member();
        updated.setMemberId(999L);

        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(updated));
    }

    @Test
    void updateNetOrderAmountAndChangeGrade_success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        BigDecimal newAmount = new BigDecimal("250000");
        memberService.updateNetOrderAmountAndChangeGrade(1L, newAmount);

        assertEquals(Grade.골드, testMember.getGrade());
    }

    @Test
    void updateNetOrderAmountAndChangeGrade_toPlatinum() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        memberService.updateNetOrderAmountAndChangeGrade(1L, new BigDecimal("1000000"));
        assertEquals(Grade.플래티넘, testMember.getGrade());
    }


    @Test
    void updateMemberStatus_changesToInactive() {
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(4));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateMemberStatus(1L);
        assertEquals(Status.INACTIVE, testMember.getStatus());
    }

    @Test
    void updateMemberStatus_recentLogin_doesNotChangeStatus() {
        // 로그인 시점이 최근(3개월 이내)
        testMember.setLastLoginAt(LocalDateTime.now().minusMonths(1));
        testMember.setStatus(Status.ACTIVE);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        memberService.updateMemberStatus(1L);

        assertEquals(Status.ACTIVE, testMember.getStatus());
    }


    @Test
    void removeMember_exists_success() {
        when(memberRepository.existsById(1L)).thenReturn(true);
        memberService.removeMember(1L);
        verify(memberRepository).deleteById(1L);
    }

    @Test
    void removeMember_notExists_throwsException() {
        when(memberRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(MemberNotFoundException.class, () -> memberService.removeMember(99L));
    }
}
