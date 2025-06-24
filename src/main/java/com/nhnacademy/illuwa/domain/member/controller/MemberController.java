package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberLoginRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원 목록 조회
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers(){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody MemberLoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.login(request));
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        MemberResponse saved = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saved);
    }

    // 회원 단일 조회
    // TODO memberId pathVaraiable로 안 받기로 고려
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId){
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getMemberById(memberId));
    }

    // 회원 수정
    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long memberId, @Valid @RequestBody MemberUpdateRequest request){
        MemberResponse updatedMemberDto = memberService.updateMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedMemberDto);
    }

    //회원 삭제
    @DeleteMapping("/{memberId}")
    public void deleteMember(@PathVariable Long memberId){
        memberService.removeMember(memberId);
    }

}
