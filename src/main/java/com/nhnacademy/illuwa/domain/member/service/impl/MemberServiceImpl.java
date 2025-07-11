package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.common.client.MemberEventPublisher;
import com.nhnacademy.illuwa.common.exception.InvalidInputException;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.GradeService;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.DeletedMemberException;
import com.nhnacademy.illuwa.domain.member.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import com.nhnacademy.illuwa.domain.point.util.PointManager;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Builder
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final GradeService gradeService;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final PointManager pointManager;
    private final MemberEventPublisher memberEventPublisher;

    @Override
    public MemberResponse register(MemberRegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank() ||
            request.getName() == null || request.getName().isBlank() ||
            request.getBirth() == null || request.getContact() == null || request.getContact().isBlank()) {
            throw new InvalidInputException("가입정보가 제대로 입력되지 않았습니다.");
        }

        //기존 존재 || 탈퇴한 회원 이메일인지 체크
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    if (member.getStatus() == Status.DELETED) {
                        throw new DeletedMemberException();
                    }
                    throw new DuplicateMemberException();
                });

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encodedPassword);

        Grade basicGrade = gradeService.getByGradeName(GradeName.BASIC);
        Member newMember = memberMapper.toEntity(request);
        newMember.changeGrade(basicGrade);

        //페이코 회원은 가입과 동시에 로그인한 걸로 기록
        if(newMember.getRole().equals(Role.PAYCO)){
            newMember.changeLastLoginAt(LocalDateTime.now());
        }
        Member saved = memberRepository.save(newMember);

        try {
            pointManager.processEventPoint(saved.getMemberId(), PointReason.JOIN);
        } catch (Exception e) {
            log.warn("회원가입 포인트 적립 실패: memberId={}, reason={}", saved.getMemberId(), e.getMessage());
        }

        try {
            memberEventPublisher.sendMemberCreateEvent(new MemberEventDto(saved.getMemberId(), saved.getEmail(), saved.getName(), saved.getBirth()));
        } catch (Exception e) {
            log.error("회원 생성 이벤트 발송 실패 -> "+ e.getMessage());
        }

        return memberMapper.toDto(saved);
    }

    @Override
    public MemberResponse login(MemberLoginRequest request) {
        Member loginMember = memberRepository.findByEmail(request.getEmail())
                        .orElseThrow(MemberNotFoundException::new);

        if(!passwordEncoder.matches(request.getPassword(), loginMember.getPassword())) {
            throw new InvalidInputException("비밀번호가 틀렸습니다.");
        }

        if(loginMember.getStatus().equals(Status.DELETED)){
            throw new DeletedMemberException();
        }
        updateMemberStatus(loginMember.getMemberId());
        loginMember.changeLastLoginAt(LocalDateTime.now());

        memberRepository.save(loginMember);  //바로 DB 반영
        return memberMapper.toDto(loginMember);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAll();
        return memberList.stream()
                .map(memberMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MemberResponse> getPagedAllMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findActiveMemberOrderByLastLoginAtOrderDesc(pageable);
        return page.map(memberMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> getAllMembersByStatus(Status status) {
        List<Member> memberList = memberRepository.findMembersByStatus(status);
        return memberList.stream()
                .map(memberMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public MemberResponse getMemberById(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        updateMemberStatus(memberId);
        return memberMapper.toDto(member);
    }

    @Override
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        return memberMapper.toDto(member);
    }

    @Override
    public MemberResponse updateMember(long memberId, MemberUpdateRequest newMemberRequest) {
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        if(newMemberRequest.getContact() != null){
            orgMember.changeContact(newMemberRequest.getContact());
        }
        if(newMemberRequest.getName() != null){
            orgMember.changeName(newMemberRequest.getName());
        }
        if(newMemberRequest.getPassword() != null){
            String newPassword = passwordEncoder.encode(newMemberRequest.getPassword());
            orgMember.changePassword(newPassword);
        }
        memberRepository.save(orgMember);
        return memberMapper.toDto(orgMember);
    }

    @Override
    public boolean checkPassword(long memberId, String inputPassword) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return passwordEncoder.matches(inputPassword, member.getPassword());
    }

    @Override
    public boolean updateMemberGrade(long memberId, BigDecimal netOrderAmount) {
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Grade newGrade = gradeService.calculateGrade(netOrderAmount);
        Grade currentGrade = orgMember.getGrade();

        if(!newGrade.equals(currentGrade)){
            orgMember.changeGrade(newGrade);
            memberRepository.save(orgMember);
            return true;
        }
        return false;
    }

    @Override
    public void updateMemberStatus(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        LocalDateTime threeMonthsAgo  = LocalDateTime.now().minusMonths(3);
        if((member.getLastLoginAt() != null && member.getLastLoginAt().isBefore(threeMonthsAgo)) ||
            (member.getLastLoginAt() == null && member.getCreatedAt().isBefore(threeMonthsAgo))){
                member.changeStatus(Status.INACTIVE);
        }
    }

    @Override
    public void reactivateMember(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        member.changeStatus(Status.ACTIVE);
    }

    @Override
    public void removeMember(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        if(member.getStatus().equals(Status.DELETED)){
            throw new DeletedMemberException();
        }
        if(!memberRepository.isNotActiveMember(memberId)){
            member.changeStatus(Status.DELETED);
        }
    }

    // 생일자 조회 서비스
    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> getMembersByBirthMonth(int month) {
        return memberRepository.findMemberByBirthMonth(month)
                .stream()
                .map(memberMapper::toDto)
                .toList();
    }
}
