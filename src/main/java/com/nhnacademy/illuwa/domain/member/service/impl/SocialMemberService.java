package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.member.utils.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialMemberService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;

    public Optional<MemberResponse> findByPaycoId(String paycoId) {
        return memberRepository.findByPaycoId(paycoId)
                .map(member -> {
                    memberService.checkMemberStatus(member.getMemberId());
                    member.setLastLoginAt(LocalDateTime.now());
                    Member saved = memberRepository.save(member);
                    return memberMapper.toDto(saved);
                });
    }

    public MemberResponse register(PaycoMemberRequest request) {
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        MemberRegisterRequest registerRequest = MemberRegisterRequest.builder()
                .paycoId(request.getIdNo())
                .role(Role.PAYCO)
                .name(request.getName())
                .password(encodedPassword)
                .email(request.getEmail())
                .birth(LocalDate.parse(request.getBirthdayMMdd()))
                .contact(request.getMobile())
                .build();

        return memberService.register(registerRequest);
    }
}
