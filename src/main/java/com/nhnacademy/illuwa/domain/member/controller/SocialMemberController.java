package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberRequest;
import com.nhnacademy.illuwa.domain.member.dto.PaycoMemberUpdateRequest;
import com.nhnacademy.illuwa.domain.member.service.impl.SocialMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members/internal/social-members")
@RequiredArgsConstructor
@Tag(name = "소셜 회원 API", description = "Payco 등 소셜 계정 기반 회원의 조회, 등록, 수정 API")
public class SocialMemberController {
    private final SocialMemberService socialMemberService;

    @Operation(summary = "소셜 회원 존재 여부 확인", description = "Payco ID로 기존 회원 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 존재"),
            @ApiResponse(responseCode = "404", description = "해당 소셜 회원 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/check")
    public ResponseEntity<MemberResponse> checkSocialUser(@RequestBody PaycoMemberRequest request) {
        return socialMemberService.findByPaycoId(request.getIdNo())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "소셜 회원 등록", description = "Payco ID 기반으로 새로운 소셜 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 등록 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터 오류"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<MemberResponse> registerSocialUser(@RequestBody PaycoMemberRequest request) {
        MemberResponse response = socialMemberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "소셜 회원 정보 수정", description = "소셜 회원의 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터 오류"),
            @ApiResponse(responseCode = "404", description = "해당 회원 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping
    public ResponseEntity<Void> updateSocialUser(@RequestHeader("X-USER-ID") long memberId, @RequestBody PaycoMemberUpdateRequest request) {
        socialMemberService.updatePaycoMember(memberId, request);
        return ResponseEntity.ok().build();
    }
}
