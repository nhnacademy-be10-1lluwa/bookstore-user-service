package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
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
    public static final LocalDate TEMPORARY_BIRTH = LocalDate.of(1000, 1, 1);


    public Optional<MemberResponse> findByPaycoId(String paycoId) {
        return memberRepository.findByPaycoId(paycoId)
                .map(member -> {
                    memberService.updateMemberStatus(member.getMemberId());
                    member.changeLastLoginAt(LocalDateTime.now());
                    Member saved = memberRepository.save(member);
                    return memberMapper.toDto(saved);
                });
    }

    public MemberResponse register(PaycoMemberRequest request) {
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        MemberRegisterRequest registerRequest = MemberRegisterRequest.builder()
                .paycoId(request.getIdNo())
                .role(Role.PAYCO)
                .name(Optional.ofNullable(request.getName()).orElse("페이코 유저"))
                .password(encodedPassword)
                .email(Optional.ofNullable(request.getEmail()).orElse("payco_user_" + UUID.randomUUID().toString().substring(0, 8) + "@payco.com"))
                .birth(parseBirthdayOrDefault(request.getBirthdayMMdd()))
                .contact(Optional.ofNullable(request.getMobile()).orElse("010-0000-0000"))
                .build();

        return memberService.register(registerRequest);
    }

    public void updatePaycoMember(long memberId, PaycoMemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        if (request.getName() != null) {
            member.changeName(request.getName());
        }
        if (request.getBirth() != null) {
            member.changeBirth(request.getBirth());
        }
        if (request.getEmail() != null) {
            member.changeEmail(request.getEmail());
        }
        if (request.getContact() != null) {
            member.changeContact(request.getContact());
        }
        memberRepository.save(member);
    }


    // MMdd 형식 아닌 경우 기본생일 반환 (TEMPORARY_BIRTH)
    private LocalDate parseBirthdayOrDefault(String birthdayMMdd) {
        if (birthdayMMdd == null || birthdayMMdd.length() != 4) {
            return TEMPORARY_BIRTH;
        }
         // "MMdd" 형식 파싱
        try {
            int month = Integer.parseInt(birthdayMMdd.substring(0, 2));
            int day = Integer.parseInt(birthdayMMdd.substring(2, 4));
            return LocalDate.of(1000, month, day);
        } catch (Exception e) {
            return TEMPORARY_BIRTH;
        }
    }

}
