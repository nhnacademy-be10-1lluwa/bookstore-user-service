package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.domain.member.exception.InvalidRequestException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }
    @Override
    @Transactional
    public Member register(Member member) {
        if (member == null ||
                member.getEmail() == null || member.getEmail().isBlank() ||
                member.getPassword() == null || member.getPassword().isBlank() ||
                member.getName() == null || member.getName().isBlank() ||
                member.getBirth() == null ||
                member.getContact() == null || member.getContact().isBlank()) {

            throw new InvalidRequestException("가입정보가 제대로 입력되지 않았습니다.");
        }

        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new DuplicateMemberException("중복된 이메일입니다: " + member.getEmail());
        }

        return memberRepository.save(member);
    }


    @Transactional
    @Override
    public Member login(MemberLoginRequest request) {
        Member loginMember = memberRepository.getMemberByEmailAndPassword(request.getEmail(), request.getPassword());
        if(loginMember == null){
            throw new MemberNotFoundException(request.getEmail());
        }
        updateMemberStatus(loginMember.getMemberId());
        loginMember.setLastLoginAt(LocalDateTime.now());
        return loginMember;
    }

    @Override
    public Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @Transactional
    @Override
    public void updateMember(Member member) {
        // 아이디값은 세션에서 받아올 예정
        // 로그인된 상태에서 사용자가 자기 정보를 수정하는게 확실하다고 가정
        Member orgMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(member.getMemberId()));
        memberMapper.updateMember(orgMember, member);
    }

    @Transactional
    @Override
    public void updateNetOrderAmountAndChangeGrade(long memberId, BigDecimal netOrderAmount) {
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Grade newGrade = Grade.calculateByAmount(netOrderAmount);
            orgMember.setGrade(newGrade);
    }

    @Override
    public void updateMemberStatus(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        LocalDateTime threeMonthsAgo  = LocalDateTime.now().minusMonths(3);
        if(member.getLastLoginAt().isBefore(threeMonthsAgo)){
            member.setStatus(Status.INACTIVE);
            //Todo 휴면해제를 위한 인증절차(두레이 메시지 Service)
        }

    }

    @Override
    public void removeMember(long memberId) {
        if(!memberRepository.existsById(memberId)){
            throw new MemberNotFoundException(memberId);
        }
        memberRepository.deleteById(memberId);
    }

}
