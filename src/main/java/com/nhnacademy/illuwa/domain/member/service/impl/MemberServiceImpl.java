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
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeRequest;
import com.nhnacademy.illuwa.domain.message.service.SendVerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final SendVerificationCodeService sendVerificationCodeService;

    @Override
    public MemberResponse register(Member member) {
        if (member == null ||
                member.getEmail() == null || member.getEmail().isBlank() ||
                member.getPassword() == null || member.getPassword().isBlank() ||
                member.getName() == null || member.getName().isBlank() ||
                member.getBirth() == null ||
                member.getContact() == null || member.getContact().isBlank()) {

            throw new InvalidRequestException("가입정보가 제대로 입력되지 않았습니다.");
        }
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new DuplicateMemberException();
        }
        return memberMapper.toDto(memberRepository.save(member));
    }

    @Override
    public MemberResponse login(MemberLoginRequest request) {
        Member loginMember = memberRepository.getMemberByEmailAndPassword(request.getEmail(), request.getPassword())
                .orElseThrow(MemberNotFoundException::new);
        checkMemberInactive(loginMember.getMemberId());
        loginMember.setLastLoginAt(LocalDateTime.now());
        return memberMapper.toDto(loginMember);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAll();
        List<MemberResponse> responseList = memberList.stream()
                .map(memberMapper::toDto)
                .toList();
        return responseList;
    }

    @Transactional(readOnly = true)
    @Override
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return memberMapper.toDto(member);
    }

    @Override
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest newMemberRequest) {
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Member updatedMember = memberMapper.updateMember(orgMember, newMemberRequest);
        return memberMapper.toDto(updatedMember);
    }

    @Override
    public void updateNetOrderAmountAndChangeGrade(Long memberId, BigDecimal netOrderAmount) {
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Grade newGrade = Grade.calculateByAmount(netOrderAmount);
            orgMember.setGrade(newGrade);
    }

    @Override
    public boolean checkMemberInactive(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        LocalDateTime threeMonthsAgo  = LocalDateTime.now().minusMonths(3);
        if(member.getLastLoginAt().isBefore(threeMonthsAgo)){
            member.setStatus(Status.INACTIVE);
            return true;
        }
        return false;
    }

    //TODO 수정/리팩토링 예정
    public void sendVerificationCodeForInactiveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberNotFoundException(memberId)
        );
        if (!member.getStatus().equals(Status.INACTIVE)) {
            throw new IllegalStateException("휴면 회원에게만 인증번호를 전송할 수 있어요!");
        }
        sendVerificationCodeService.sendVerificationNumber(
                new SendVerificationCodeRequest(member.getEmail(), "test", null)
        );
    }

    @Override
    public void reactivateMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        member.setStatus(Status.ACTIVE);
    }

    @Override
    public void removeMember(Long memberId) {
        if(!memberRepository.existsById(memberId)){
            throw new MemberNotFoundException(memberId);
        }
        memberRepository.deleteById(memberId);
    }
}
