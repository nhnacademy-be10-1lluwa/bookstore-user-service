package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
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
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원 목록 조회
    @GetMapping("/api/admin/members")
    public ResponseEntity<List<MemberResponse>> getMemberList() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }

    // 회원 목록 조회 (등급별 조회 가능)
    @GetMapping("/api/admin/members/paged")
    public ResponseEntity<Page<MemberResponse>> getPagedMemberList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "grade", required = false) GradeName gradeName
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastLoginAt").descending());
        Page<MemberResponse> memberPage;

        if (gradeName != null) {
            memberPage = memberService.getPagedAllMembersByGradeName(gradeName, pageable);
        } else {
            memberPage = memberService.getPagedAllMembers(pageable);
        }

        return ResponseEntity.ok(memberPage);
    }


    // 회원가입
    @PostMapping("/api/members")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        MemberResponse saved = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    // 로그인
    @PostMapping("/api/members/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(request));
    }

    // 회원 단일 조회
    @GetMapping("/api/members")
    public ResponseEntity<MemberResponse> getMember(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMemberById(memberId));
    }

    // 회원 수정
    @PutMapping("/api/members")
    public ResponseEntity<MemberResponse> updateMember(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberUpdateRequest request) {
        MemberResponse updatedMemberDto = memberService.updateMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedMemberDto);
    }

    // 회원 비밀번호 체크
    @PostMapping("/api/members/check-pw")
    public ResponseEntity<Boolean> checkPassword(@RequestHeader("X-USER-ID") long memberId, @RequestBody PasswordCheckRequest request) {
        boolean isEqual = memberService.checkPassword(memberId, request.getInputPassword());
        return ResponseEntity.ok(isEqual);
    }

    //회원 삭제
    @DeleteMapping("/api/members")
    public void deleteMember(@RequestHeader("X-USER-ID") long memberId) {
        memberService.removeMember(memberId);
    }

    // 이번 달 생일 회원 조회
    @GetMapping(value = "/api/members/birth-month", params = "month")
    public ResponseEntity<List<MemberResponse>> getMemberByBirthMonth(@RequestParam int month) {
        return ResponseEntity.ok(memberService.getMembersByBirthMonth(month));
    }

    @PostMapping(value = "/api/members/names")
    public ResponseEntity<Map<Long, String>> getNamesFromIdList(@RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(memberService.getNamesFromIdList(memberIds));
    }
}