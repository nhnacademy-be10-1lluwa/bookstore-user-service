package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원 목록 조회
    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberResponse>> getMemberList() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }

    // 회원 목록 조회
    @GetMapping("/admin/members/paged")
    public ResponseEntity<Page<MemberResponse>> getPagedMemberList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastLoginAt").descending());
        Page<MemberResponse> memberPage = memberService.getPagedAllMembers(pageable);

        return ResponseEntity.ok(memberPage);
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
    @PutMapping("/members")
    public ResponseEntity<MemberResponse> updateMember(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberUpdateRequest request) {
        MemberResponse updatedMemberDto = memberService.updateMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedMemberDto);
    }

    // 회원 비밀번호 체크
    @PostMapping("/members/check-pw")
    public ResponseEntity<Boolean> checkPassword(@RequestHeader("X-USER-ID") long memberId, @RequestBody PasswordCheckRequest request) {
        log.info("비밀번호 체크 요청 - memberId: {}, password: {}", memberId, request.getInputPassword());
        boolean isEqual = memberService.checkPassword(memberId, request.getInputPassword());
        log.info("검증 결과: {}", isEqual);
        return ResponseEntity.ok(isEqual);
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
