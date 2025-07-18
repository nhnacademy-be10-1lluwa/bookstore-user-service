package com.nhnacademy.illuwa.domain.member.controller;

import com.nhnacademy.illuwa.domain.member.dto.InactiveCheckResponse;
import com.nhnacademy.illuwa.domain.member.dto.SendVerificationRequest;
import com.nhnacademy.illuwa.domain.member.dto.VerifyCodeRequest;
import com.nhnacademy.illuwa.domain.member.dto.VerifyCodeResponse;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.*;
import com.nhnacademy.illuwa.domain.message.service.InactiveVerificationService;
import com.nhnacademy.illuwa.domain.message.service.MessageService;
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
@RequestMapping("/api/members/inactive")
public class InactiveMemberController {
    private final MemberService memberService;
    private final MessageService messageService;
    private final InactiveVerificationService inactiveVerificationService;

    //회원 휴면상태 체크
    @PostMapping("/check-status")
    public ResponseEntity<InactiveCheckResponse> getInactiveMemberInfo(@RequestBody SendVerificationRequest request){
        InactiveCheckResponse member = getMemberOrThrow(request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    //인증번호 전송 api
    @PostMapping("/code")
    public ResponseEntity<SendMessageResponse> sendVerificationCode(@RequestBody SendVerificationRequest request) {
        InactiveCheckResponse member = getMemberOrThrow(request.getEmail());

        SendMessageRequest messageRequest = new SendMessageRequest();
        messageRequest.setRecipientName(member.getName());
        messageRequest.setRecipientEmail(member.getEmail());

        SendMessageResponse response = messageService.sendVerificationCode(messageRequest);
        return ResponseEntity.ok().body(response);
    }

    //인증번호 검증 api
    @PostMapping("/verification")
    public ResponseEntity<VerifyCodeResponse> receiveVerificationCode(@RequestBody VerifyCodeRequest request) {
        InactiveCheckResponse member = getMemberOrThrow(request.getEmail());
        String email = member.getEmail();

        if (inactiveVerificationService.verifyAndReactivateMember(member.getMemberId(), email, request.getCode())) {
            return ResponseEntity.ok(
                    new VerifyCodeResponse(true,member.getMemberId(), email, "인증에 성공하여 휴면상태가 해제됐어요")
            );
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new VerifyCodeResponse(false, member.getMemberId(), email, "인증에 실패하여 휴면상태가 지속됩니다.")
            );
        }
    }

    public InactiveCheckResponse getMemberOrThrow(String email) {
        try {
            return memberService.getInactiveMemberInfoByEmail(email);
        } catch (RuntimeException e) {
            throw new MemberNotFoundException();
        }
    }
}
