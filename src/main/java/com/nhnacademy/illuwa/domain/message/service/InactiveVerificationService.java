package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InactiveVerificationService {
    private final MemberService memberService;
    private final MessageService messageService;

    private final RedisTemplate<String, String> redisTemplate;

    public boolean verifyAndReactivateMember(long memberId, String email, String code) {
        boolean isVerified = verifyCode(email, code);

        String name = memberService.getMemberById(memberId).getName();
        if (isVerified) {
            memberService.reactivateMember(memberId);

            SendMessageRequest successRequest = SendMessageRequest.builder()
                    .attachmentTitle("환영합니다!🥳")
                    .attachmentText(name +"님, 휴면이 성공적으로 해제됐습니다.")
                    .build();
            messageService.sendDoorayMessage(successRequest);
            return true;
        }
        SendMessageRequest failRequest = SendMessageRequest.builder()
                .attachmentTitle(name +"님, 인증번호를 다시 확인해주세요 😢")
                .build();
        messageService.sendDoorayMessage(failRequest);
        return false;
    }

    public boolean verifyCode(String email, String inputCode) {
        String key = "verify:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        return inputCode != null && inputCode.equals(storedCode);
    }

}
