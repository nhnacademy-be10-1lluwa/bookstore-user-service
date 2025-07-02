package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageResponse;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeRequest;
import com.nhnacademy.illuwa.domain.message.dto.VerifyCodeResponse;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/inactive/verification")
public class InactiveVerificationController {

    private final MemberService memberService;
    private final MessageSendService messageSendService;
    private final InactiveVerificationService inactiveVerificationService;

    //인증번호 전송 api
    @PostMapping
    public ResponseEntity<SendMessageResponse> sendVerificationCode(@RequestHeader("X-USER-ID") long memberId) {
        MemberResponse member = getMemberOrThrow(memberId);

        SendMessageRequest request = new SendMessageRequest();
        request.setRecipientName(member.getName());
        request.setRecipientEmail(member.getEmail());

        messageSendService.sendVerificationCode(request);
        return ResponseEntity.ok(
                new SendMessageResponse(member.getEmail(), "인증번호를 발송했습니다!")
        );
    }

    //인증번호 검증 api
    @PostMapping("/verify")
    public ResponseEntity<VerifyCodeResponse> receiveVerificationCode(@RequestHeader("X-USER-ID") long memberId,
                                                                      @RequestBody VerifyCodeRequest request) {
        MemberResponse member = getMemberOrThrow(memberId);
        String email = member.getEmail();

        if (inactiveVerificationService.verifyAndReactivateMember(memberId, email, request.getCode())) {
            return ResponseEntity.ok(
                    new VerifyCodeResponse(memberId, email, "인증에 성공하여 휴면상태가 해제됐어요")
            );
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new VerifyCodeResponse(memberId, email, "인증에 실패하여 휴면상태가 지속됩니다.")
            );
        }
    }

    public MemberResponse getMemberOrThrow(long memberId) {
        try {
            return memberService.getMemberById(memberId);
        } catch (RuntimeException e) {
            throw new MemberNotFoundException(memberId);
        }
    }
}
