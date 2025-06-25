package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.service.impl.SocialMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class SocialMemberController {
    private final SocialMemberService socialMemberService;

    @PostMapping("/social-login")
    public ResponseEntity<MemberResponse> loginOrRegister(@RequestBody PaycoMemberRequest request){
        MemberResponse response = socialMemberService.loginOrRegister(request);
        return ResponseEntity.ok().body(response);
    }
}
