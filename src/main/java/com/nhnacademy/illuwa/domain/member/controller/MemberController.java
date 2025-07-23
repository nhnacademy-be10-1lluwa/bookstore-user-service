package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.member.dto.*;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회원 API", description = "회원 가입, 로그인, 수정, 삭제 및 관리 기능 제공")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 목록 조회 (관리자)", description = "등급 필터링 및 페이징 처리된 회원 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/api/admin/members")
    public ResponseEntity<Page<MemberResponse>> getMemberList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "grade", required = false) GradeName gradeName
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastLoginAt").descending());

        Page<MemberResponse> memberPage = (gradeName != null)
                ? memberService.getPagedAllMembersByGradeName(gradeName, pageable)
                : memberService.getPagedAllMembers(pageable);

        return ResponseEntity.ok(memberPage);
    }

    @Operation(summary = "회원가입", description = "신규 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/members")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        MemberResponse saved = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "회원 로그인", description = "회원 로그인 후 회원 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/api/members/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        return ResponseEntity.ok(memberService.login(request));
    }

    @Operation(summary = "회원 단일 조회", description = "헤더에 포함된 사용자 ID를 기준으로 회원 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원 없음")
    })
    @GetMapping("/api/members")
    public ResponseEntity<MemberResponse> getMember(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

    @Operation(summary = "회원 수정", description = "회원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값"),
            @ApiResponse(responseCode = "404", description = "해당 회원 없음")
    })
    @PutMapping("/api/members")
    public ResponseEntity<MemberResponse> updateMember(@RequestHeader("X-USER-ID") long memberId, @Valid @RequestBody MemberUpdateRequest request) {
        return ResponseEntity.ok(memberService.updateMember(memberId, request));
    }

    @Operation(summary = "회원 비밀번호 확인", description = "입력된 비밀번호가 기존 비밀번호와 일치하는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 일치 여부 반환"),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
    })
    @PostMapping("/api/members/password-check")
    public ResponseEntity<Boolean> checkPassword(@RequestHeader("X-USER-ID") long memberId, @RequestBody PasswordCheckRequest request) {
        boolean isEqual = memberService.checkPassword(memberId, request.getInputPassword());
        return ResponseEntity.ok(isEqual);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원 없음")
    })
    @DeleteMapping("/api/members")
    public void deleteMember(@RequestHeader("X-USER-ID") long memberId) {
        memberService.removeMember(memberId);
    }

    @Operation(summary = "생일자 회원 조회", description = "이번 달이 생일인 회원 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생일자 조회 성공")
    })
    @GetMapping(value = "/api/members/birth-month", params = "month")
    public ResponseEntity<List<MemberResponse>> getMemberByBirthMonth(@RequestParam int month) {
        return ResponseEntity.ok(memberService.getMembersByBirthMonth(month));
    }

    @Operation(summary = "회원 이름 조회", description = "회원 ID 리스트를 기반으로 이름 맵을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이름 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @GetMapping("/api/members/names")
    public ResponseEntity<Map<Long, String>> getNamesFromIdList(@RequestParam("member-ids") List<Long> ids) {
        return ResponseEntity.ok(memberService.getNamesFromIdList(ids));
    }
}