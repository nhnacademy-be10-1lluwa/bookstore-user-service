package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.impl.SocialMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members/internal/social-members")
@RequiredArgsConstructor
public class SocialMemberController {
    private final SocialMemberService socialMemberService;

    @PostMapping("/check")
    public ResponseEntity<MemberResponse> checkSocialUser(@RequestBody PaycoMemberRequest request) {
        return socialMemberService.findByPaycoId(request.getIdNo())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<MemberResponse> registerSocialUser(@RequestBody PaycoMemberRequest request) {
        MemberResponse response = socialMemberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<Void>  updateSocialUser(@RequestHeader("X-USER-ID") long memberId, @RequestBody PaycoMemberUpdateRequest request) {
        socialMemberService.updatePaycoMember(memberId, request);
        return ResponseEntity.ok().build();
    }
}

