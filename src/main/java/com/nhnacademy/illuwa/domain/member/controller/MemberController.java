package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원 목록 조회
    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }

    // 회원가입
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        MemberResponse saved = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    // 로그인
    @PostMapping("/members/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(request));
    }

    // 회원 단일 조회
    @GetMapping("/members")
    public ResponseEntity<MemberResponse> getMember(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMemberById(memberId));
    }

    // 회원 수정
    @PatchMapping("/members")
    public ResponseEntity<MemberResponse> updateMember(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberUpdateRequest request) {
        MemberResponse updatedMemberDto = memberService.updateMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedMemberDto);
    }

    //회원 삭제
    @DeleteMapping("/members")
    public void deleteMember(@RequestHeader("X-USER-ID") long memberId) {
        memberService.removeMember(memberId);
    }

    // 이번 달 생일 회원 조회
    @GetMapping(value = "/members/birth-month", params = "month")
    public ResponseEntity<List<MemberResponse>> getMemberByBirthMonth(@RequestParam int month) {
        return ResponseEntity.ok(memberService.getMembersByBirthMonth(month));
    }
}
