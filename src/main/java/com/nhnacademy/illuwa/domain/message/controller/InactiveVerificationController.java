package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeResponse;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeRequest;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeResponse;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.SendVerificationCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{memberId}/inactive/verification-code")
public class InactiveVerificationController {

    private final MemberService memberService;
    private final SendVerificationCodeService sendCodeService;
    private final InactiveVerificationService verificationService;

    public InactiveVerificationController(MemberService memberService, SendVerificationCodeService sendCodeService,
                                          InactiveVerificationService verificationService) {
        this.memberService = memberService;
        this.sendCodeService = sendCodeService;
        this.verificationService = verificationService;
    }

    @PostMapping
    public ResponseEntity<SendVerificationCodeResponse> sendVerificationCode(@PathVariable long memberId) {
        String email = getMemberEmailOrThrow(memberId);

        try {
            SendVerificationCodeRequest request = new SendVerificationCodeRequest();
            request.setEmail(email);
            sendCodeService.sendVerificationNumber(request);
            return ResponseEntity.ok(new SendVerificationCodeResponse(memberId, email, "인증번호 메시지가 성공적으로 전송됐어요"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SendVerificationCodeResponse(memberId, email, "인증번호 전송에 실패했어요"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyCodeResponse> receiveVerificationCode(@PathVariable long memberId,
                                                                      @RequestBody VerifyCodeRequest request) {
        String email = getMemberEmailOrThrow(memberId);

        if (verificationService.verifyCode(email, request.getCode())) {
            memberService.reactivateMember(memberId);
            return ResponseEntity.ok(new VerifyCodeResponse(memberId, email, "인증에 성공하여 휴면상태가 해제됐어요"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new VerifyCodeResponse(memberId, email, "인증에 실패하여 휴면상태가 지속됩니다."));
        }
    }

    private String getMemberEmailOrThrow(long memberId) {
        try {
            return memberService.getMemberById(memberId).getEmail();
        } catch (RuntimeException e) {
            throw new MemberNotFoundException(memberId);
        }
    }
}
