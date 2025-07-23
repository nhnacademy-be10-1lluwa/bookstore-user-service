package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.InactiveCheckResponse;
import com.nhnacademy.illuwa.domain.member.dto.SendVerificationRequest;
import com.nhnacademy.illuwa.domain.member.dto.VerifyCodeRequest;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.member.service.impl.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/inactive-verifications")
@Tag(name = "휴면회원 인증 API", description = "휴면 회원의 전화번호 인증 및 계정 복구 관련 API")
public class InactiveMemberController {
    private final MemberService memberService;
    private final MessageService messageService;
    private final InactiveVerificationService inactiveVerificationService;

    // 인증번호 전송 API
    @PostMapping
    @Operation(summary = "인증번호 전송", description = "입력한 연락처(전화번호 or 이메일)에 인증번호를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 전송 성공"),
            @ApiResponse(responseCode = "404", description = "휴면 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Boolean> sendVerificationCode(@RequestBody SendVerificationRequest request) {
        InactiveCheckResponse member = getMemberOrThrow(request.getContact());

        SendMessageRequest messageRequest = new SendMessageRequest();
        messageRequest.setRecipientName(member.getName());
        messageRequest.setRecipientEmail(member.getEmail());

        SendMessageResponse response = messageService.sendVerificationCode(messageRequest);
        return ResponseEntity.ok().body(response.isSuccess());
    }

    // 인증번호 검증 API
    @PostMapping("/verify")
    @Operation(summary = "인증번호 검증", description = "입력한 인증번호를 검증하고 계정을 복구합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 검증 성공 및 계정 복구 완료"),
            @ApiResponse(responseCode = "401", description = "인증번호 불일치로 검증 실패"),
            @ApiResponse(responseCode = "404", description = "휴면 회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Boolean> receiveVerificationCode(@RequestBody VerifyCodeRequest request) {
        InactiveCheckResponse member = getMemberOrThrow(request.getContact());
        String email = member.getEmail();

        boolean verified = inactiveVerificationService.verifyAndReactivateMember(member.getMemberId(), email, request.getCode());
        if (verified) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    public InactiveCheckResponse getMemberOrThrow(String contact) {
        try {
            return memberService.getInactiveMemberInfoByContact(contact);
        } catch (RuntimeException e) {
            throw new MemberNotFoundException();
        }
    }
}
