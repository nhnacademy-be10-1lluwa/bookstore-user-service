package com.nhnacademy.illuwa.domain.member.service.impl;

import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialMemberService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse loginOrRegister(PaycoMemberRequest request){
        MemberResponse exisitngMemberResponse = memberService.getMemberByEmail(request.getEmail());

        if(exisitngMemberResponse != null){
            return exisitngMemberResponse;
        }

        int birthMM = Integer.parseInt(request.getBirthdayMMdd().substring(0,2));
        int birthDD = Integer.parseInt(request.getBirthdayMMdd().substring(2,4));

        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        MemberRegisterRequest newMemberRequest = MemberRegisterRequest.builder()
                    .name(request.getName())
                    .password(encodedPassword)
                    .email(request.getEmail())
                    .birth(LocalDate.of(0, birthDD, birthMM))
                    .contact(request.getMobile())
                    .build();

        return memberService.register(newMemberRequest);
    }
}
