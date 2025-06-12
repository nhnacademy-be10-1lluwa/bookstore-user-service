package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeResponse;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeRequest;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeResponse;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{memberId}/inactive/verification-code")
public class InactiveVerificationController {

    private final MemberService memberService;
    private final SendMessageService sendMessageService;
    private final InactiveVerificationService verificationService;

    @Autowired
    public InactiveVerificationController(MemberService memberService, SendMessageService sendMessageService, InactiveVerificationService verificationService) {
        this.memberService = memberService;
        this.sendMessageService = sendMessageService;
        this.verificationService = verificationService;
    }

    @PostMapping
    public ResponseEntity<SendVerificationCodeResponse> sendVerificationCode(@PathVariable long memberId) {
        String memberEmail;
        try {
            memberEmail = memberService.getMemberById(memberId).getEmail();
        } catch (RuntimeException e) {
            throw new MemberNotFoundException(memberId);
        }
        SendVerificationCodeRequest request= new SendVerificationCodeRequest();
        request.setEmail(memberEmail);

        boolean result = true;
        String message = "인증번호 메시지가 성공적으로 전송됐어요";
        try {
            sendMessageService.sendVerificationNumber(request);
        } catch (RuntimeException e){
            result = false;
            message = "인증번호 전송에 실패했어요";
        }

        SendVerificationCodeResponse response = new SendVerificationCodeResponse(
                result,
                memberId,
                memberEmail,
                message
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyCodeResponse> receiveVerificationCode(@PathVariable long memberId, @RequestBody VerifyCodeRequest request) {
        Member member = memberService.getMemberById(memberId);
        String memberEmail;
        try {
            memberEmail = member.getEmail();
        } catch (RuntimeException e) {
            throw new MemberNotFoundException(memberId);
        }

        request.setMemberId(memberId);
        boolean result = verificationService.verifyCode(memberEmail, request.getCode());

        String message;
        if(result){
            memberService.reactivateMember(memberId);
            message = "인증에 성공하여 휴면상태가 해제됐어요";
        }else{
            message = "인증에 실패하여 휴면상태가 지속됩니다.";
        }

        VerifyCodeResponse response = new VerifyCodeResponse(
                result,
                memberId,
                memberEmail,
                message
        );

        return ResponseEntity.ok(response);
    }
}
