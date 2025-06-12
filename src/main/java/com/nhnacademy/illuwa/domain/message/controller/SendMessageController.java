package com.nhnacademy.illuwa.domain.message.controller;

import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.InactiveRestoreResponse;
import com.nhnacademy.illuwa.domain.message.dto.InactiveVerificationRequest;
import com.nhnacademy.illuwa.domain.message.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMessageController {

    private final MemberService memberService;
    private final SendMessageService sendMessageService;

    @Autowired
    public SendMessageController(MemberService memberService, SendMessageService sendMessageService){
        this.memberService = memberService;
        this.sendMessageService = sendMessageService;
    }

    @PostMapping("/members/{memberId}/inactive/verification-code")
    public ResponseEntity<InactiveRestoreResponse> sendVerificationCode(@PathVariable long memberId, @RequestBody InactiveVerificationRequest request){
        String memberEmail;
        try{
            memberEmail = memberService.getMemberById(memberId).getEmail();
        } catch (RuntimeException e){
            throw new MemberNotFoundException(memberId);
        }
        request.setEmail(memberEmail);
        sendMessageService.sendVerificationNumber(request);

        InactiveRestoreResponse response = new InactiveRestoreResponse(
                true,
                memberId,
                "인증번호 메시지가 성공적으로 전송됐어요!"
        );

        return ResponseEntity.ok(response);
    }
}
