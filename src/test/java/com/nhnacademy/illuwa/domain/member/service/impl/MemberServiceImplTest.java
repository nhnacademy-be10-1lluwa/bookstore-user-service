package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.common.client.MemberEventPublisher;
import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.GradeService;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.DeletedMemberException;
import com.nhnacademy.illuwa.domain.member.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapperImpl;
import com.nhnacademy.illuwa.domain.point.utils.PointManager;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    MemberMapper memberMapper = new MemberMapperImpl();

    @Mock MemberRepository memberRepository;
    @Mock GradeService gradeService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock PointManager pointManager;
    @Mock MemberEventPublisher memberEventPublisher;
    @InjectMocks MemberServiceImpl memberService;

    MemberRegisterRequest registerRequest;
    Member testMember;
    Grade basicGrade;

    void setMemberId(Member member) {
        try {
            Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(member, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void setCreatedAt(Member member, LocalDateTime time) {
        try {
            Field field = Member.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(member, time);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        this.memberService = new MemberServiceImpl(
                memberRepository, gradeService, memberMapper, passwordEncoder, pointManager, memberEventPublisher
        );

        basicGrade = Grade.builder()
                .gradeName(GradeName.BASIC)
                .priority(4)
                .pointRate(BigDecimal.valueOf(0.01))
                .minAmount(BigDecimal.ZERO)
                .maxAmount(BigDecimal.valueOf(100000))
                .build();

        registerRequest = MemberRegisterRequest.builder()
                .name("카리나")
                .birth(LocalDate.of(2000, 4, 11))
                .email("karina@test.com")
                .password("Pass1234!")
                .contact("010-1234-5678")
                .build();

        testMember = Member.builder()
                .name(registerRequest.getName())
                .birth(registerRequest.getBirth())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .contact(registerRequest.getContact())
                .grade(basicGrade)
                .point(BigDecimal.ZERO)
                .status(Status.ACTIVE)
                .build();
        setMemberId(testMember);
    }

    @Test
    @DisplayName("회원 가입 성공")
    void register_success(){
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(gradeService.getByGradeName(GradeName.BASIC)).thenReturn(basicGrade);
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse response = memberService.register(registerRequest);
        assertThat(response.getMemberId()).isEqualTo(1L);
        verify(pointManager, times(1)).processEventPoint(response.getMemberId(), PointReason.JOIN, null);
        verify(memberEventPublisher, times(1)).sendMemberCreateEvent(any(MemberEventDto.class));
    }

    @Test
    @DisplayName("회원 가입 중복 이메일 예외")
    void register_duplicate_throwsException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        assertThrows(DuplicateMemberException.class, () -> memberService.register(registerRequest));
    }

    @Test
    @DisplayName("회원가입 실패 - 입력 누락")
    void register_invalidInput_throwsException() {
        MemberRegisterRequest invalidRequest = MemberRegisterRequest.builder()
                .email("")
                .password("Pass1234!")
                .name("카리나")
                .birth(null)
                .contact(null)
                .build();

        assertThrows(InvalidInputException.class, () -> memberService.register(invalidRequest));
    }

    @Test
    @DisplayName("회원가입 실패 - 삭제된 회원 이메일 사용")
    void register_deletedEmail_throwsException() {
        testMember.changeStatus(Status.DELETED);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        assertThrows(DeletedMemberException.class, () -> memberService.register(registerRequest));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any())).thenReturn(testMember);
        MemberLoginRequest loginRequest = new MemberLoginRequest(testMember.getEmail(), testMember.getPassword());

        when(passwordEncoder.matches(loginRequest.getPassword(), testMember.getPassword())).thenReturn(true);
        MemberResponse result = memberService.login(loginRequest);

        assertThat(result.getEmail()).isEqualTo(testMember.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_wrongPassword_throwsException() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        MemberLoginRequest loginRequest = new MemberLoginRequest(testMember.getEmail(), "wrong");
        assertThrows(InvalidInputException.class, () -> memberService.login(loginRequest));
    }

    @Test
    @DisplayName("로그인 실패 - 탈퇴된 회원")
    void login_deletedMember_throwsException() {
        testMember.changeStatus(Status.DELETED);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        MemberLoginRequest loginRequest = new MemberLoginRequest(testMember.getEmail(), "Pass1234!");
        assertThrows(DeletedMemberException.class, () -> memberService.login(loginRequest));
    }


    @Test
    @DisplayName("전체 회원 조회 성공")
    void getAllMembers_success() {
        when(memberRepository.findAll()).thenReturn(List.of(testMember));
        List<MemberResponse> result = memberService.getAllMembers();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("회원 ID 조회 성공")
    void getMemberById_success() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        MemberResponse result = memberService.getMemberById(1L);
        assertThat(result.getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("회원 수정 성공")
    void updateMember_success() {
        MemberUpdateRequest updateRequest = new MemberUpdateRequest();
        updateRequest.setName("윈터");
        updateRequest.setContact("010-9999-8888");

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        testMember.changeName("윈터");
        testMember.changeContact("010-9999-8888");
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse result = memberService.updateMember(1L, updateRequest);
        assertThat(result.getName()).isEqualTo("윈터");
    }

    @Test
    @DisplayName("회원 정보 수정 - 아무것도 변경하지 않음")
    void updateMember_noChange() {
        MemberUpdateRequest emptyUpdate = new MemberUpdateRequest();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        MemberResponse result = memberService.updateMember(1L, emptyUpdate);
        assertThat(result.getName()).isEqualTo(testMember.getName());
    }


    @Test
    @DisplayName("회원 등급 변경 성공")
    void updateMemberGrade_success() {
        Grade gold = Grade.builder().gradeName(GradeName.GOLD).build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(gradeService.calculateGrade(any())).thenReturn(gold);
        memberService.updateMemberGrade(1L, new BigDecimal("500000"));
        assertThat(testMember.getGrade().getGradeName()).isEqualTo(GradeName.GOLD);
    }

    @Test
    @DisplayName("회원 등급 변경 없음")
    void updateMemberGrade_noChange() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(gradeService.calculateGrade(any())).thenReturn(basicGrade);

        boolean result = memberService.updateMemberGrade(1L, BigDecimal.ZERO);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원 상태 체크 - 비활성화")
    void checkMemberStatus_inactive() {
        testMember.changeLastLoginAt(LocalDateTime.now().minusMonths(4));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        memberService.updateMemberStatus(1L);
        assertThat(testMember.getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("회원 재활성화 성공")
    void reactivateMember_success() {
        testMember.changeStatus(Status.INACTIVE);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        memberService.reactivateMember(1L);
        assertThat(testMember.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("회원 상태 체크 - 로그인 기록 없음, 생성 3개월 전")
    void checkMemberStatus_createdOld() {
        testMember.changeLastLoginAt(null);
        setCreatedAt(testMember, LocalDateTime.now().minusMonths(4));

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        memberService.updateMemberStatus(1L);
        assertThat(testMember.getStatus()).isEqualTo(Status.INACTIVE);
    }


    @Test
    @DisplayName("회원 삭제 성공")
    void removeMember_success() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        memberService.removeMember(1L);
        assertEquals(Status.DELETED, testMember.getStatus());
    }

    @Test
    @DisplayName("회원 삭제 실패 - 존재하지 않음")
    void removeMember_notExists_throwsException() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class, () -> memberService.removeMember(999L));
    }

    @Test
    @DisplayName("회원 삭제 실패 - 이미 삭제된 상태")
    void removeMember_alreadyDeleted_throwsException() {
        testMember.changeStatus(Status.DELETED);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));

        assertThrows(DeletedMemberException.class, () -> memberService.removeMember(1L));
    }

    @Test
    @DisplayName("비밀번호 일치 확인")
    void checkPassword_success() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(eq("Pass1234!"), anyString())).thenReturn(true);

        boolean result = memberService.checkPassword(1L, "Pass1234!");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비밀번호 불일치")
    void checkPassword_fail() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(eq("wrong"), anyString())).thenReturn(false);

        boolean result = memberService.checkPassword(1L, "wrong");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("휴면 회원 정보 조회 성공")
    void getInactiveMemberInfo_success() {
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        InactiveCheckResponse response = memberService.getInactiveMemberInfoByEmail(testMember.getEmail());

        assertThat(response.getEmail()).isEqualTo(testMember.getEmail());
        assertThat(response.getStatus()).isEqualTo(testMember.getStatus());
    }

    @Test
    @DisplayName("페이지 회원 목록 조회")
    void getPagedAllMembers_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Member> memberPage = new PageImpl<>(List.of(testMember), pageable, 1);

        when(memberRepository.findMemberOrderByLastLoginAtOrderDesc(any())).thenReturn(memberPage);

        Page<MemberResponse> result = memberService.getPagedAllMembers(pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("페이지 회원 목록 조회 - 등급별")
    void getPagedAllMembersByGradeName_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Member> memberPage = new PageImpl<>(List.of(testMember), pageable, 1);

        when(memberRepository.findMemberByGradeNameOrderByLastLoginAtOrderDesc(any(), any())).thenReturn(memberPage);

        Page<MemberResponse> result = memberService.getPagedAllMembersByGradeName(GradeName.BASIC, pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getGradeName()).isEqualTo("BASIC");
    }

    @Test
    @DisplayName("회원 등급으로 전체 조회")
    void getMembersByGradeName_success() {
        when(memberRepository.findByGradeName(GradeName.BASIC)).thenReturn(List.of(testMember));
        List<MemberResponse> result = memberService.getMembersByGradeName(GradeName.BASIC);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getGradeName()).isEqualTo("BASIC");
    }

    @Test
    @DisplayName("회원 상태로 조회")
    void getMembersByStatus_success() {
        when(memberRepository.findMembersByStatus(Status.ACTIVE)).thenReturn(List.of(testMember));
        List<MemberResponse> result = memberService.getMembersByStatus(Status.ACTIVE);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("생일자 조회 성공")
    void getMembersByBirthMonth_success() {
        when(memberRepository.findMemberByBirthMonth(4)).thenReturn(List.of(testMember));
        List<MemberResponse> result = memberService.getMembersByBirthMonth(4);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getBirth().getMonthValue()).isEqualTo(4);
    }
}