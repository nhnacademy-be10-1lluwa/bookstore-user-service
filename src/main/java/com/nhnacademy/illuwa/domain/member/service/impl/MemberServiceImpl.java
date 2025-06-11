package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.exception.MemberAuthenticationFailedException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.exception.MemberRegistrationException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public Member register(Member member) {
        if(member == null){
            throw new MemberRegistrationException("가입정보가 제대로 입력되지 않았습니다.");
        }
        return memberRepository.save(member);
    }

    @Transactional
    @Override
    public Member login(MemberLoginRequest request) {
        Member loginMember = memberRepository.getMemberByEmailAndPassword(request.getEmail(), request.getPassword());
        if(loginMember == null){
            throw new MemberAuthenticationFailedException();
        }
        loginMember.setLastLoginAt(LocalDateTime.now());
        return loginMember;
    }

    @Override
    public Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 아이디를 가진 회원이 존재하지 않습니다.") );
    }

    @Transactional
    @Override
    public void updateMember(Member member) {
        // 아이디값은 세션에서 받아올 예정
        // 로그인된 상태에서 사용자가 자기 정보를 수정하는게 확실하다고 가정
        Member orgMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new NoSuchElementException("해당 회원이 존재하지 않아서 정보수정에 실패했습니다."));
        memberMapper.updateMember(orgMember, member);
    }

    @Transactional
    @Override
    public void updateNetOrderAmountAndChangeGrade(long memberId, BigDecimal netOrderAmount) {
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 회원이 존재하지 않아서 등급 업데이트에 실패했습니다."));
        Grade newGrade = Grade.calculateByAmount(netOrderAmount);
            orgMember.setGrade(newGrade);
    }

    @Override
    public void updateMemberStatus(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 회원이 존재하지 않아서 상태 업데이트에 실패했습니다."));
        LocalDateTime threeMonthsAgo  = LocalDateTime.now().minusMonths(3);
        if(member.getLastLoginAt().isBefore(threeMonthsAgo)){
            member.setStatus(Status.INACTIVE);
            //Todo 휴면해제를 위한 인증절차(두레이 메시지 Sender)
        }

    }

    @Override
    public void removeMember(long memberId) {
        if(!memberRepository.existsById(memberId)){
            throw new NoSuchElementException("해당 회원이 존재하지 않아서 탈퇴에 실패했습니다.");
        }
        memberRepository.deleteById(memberId);
    }


}
