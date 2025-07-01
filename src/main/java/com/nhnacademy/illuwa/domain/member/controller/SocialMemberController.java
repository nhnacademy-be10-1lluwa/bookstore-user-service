package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.service.impl.SocialMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/members/social")
@RequiredArgsConstructor
public class SocialMemberController {
    private final SocialMemberService socialMemberService;

    @PostMapping("/check")
    public ResponseEntity<MemberResponse> checkSocialUser(@RequestBody PaycoMemberRequest request) {
        return socialMemberService.findByPaycoId(request.getIdNo())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerSocialUser(@RequestBody PaycoMemberRequest request) {
        MemberResponse response = socialMemberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

