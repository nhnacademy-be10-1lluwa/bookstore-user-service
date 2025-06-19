package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.common.client.DoorayMessageClient;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final DoorayMessageClient doorayMessageClient;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    //최상위 두레이메시지전송 메서드
    public void sendDoorayMessage(SendMessageRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("botName", request.getBotName());
        if(request.getText() != null){
            body.put("text", request.getText());
        }

        if(request.hasAttachment()){
            Map<String, Object> attachment = new HashMap<>();

            attachment.put("title", request.getAttachmentTitle());
            attachment.put("text", request.getAttachmentText());
            attachment.put("color", request.getAttachmentColor());

            body.put("attachments", List.of(attachment));
        }

        try {
            doorayMessageClient.sendMessage(body);
            log.debug("두레이 메시지 전송 성공!");
        } catch (Exception e) {
            log.error("두레이 메시지 전송 실패: {}", e.getMessage());
        }
    }

    //주문완료 메시지
    public void sendOrderMessage(SendMessageRequest request, String orderNumber) {
        request.setText(request.getRecipientName() + "님의 소중한 주문이 완료되었습니다!😎");
        request.setAttachmentTitle("🎁주문완료");
        request.setAttachmentText("주문번호: " + "[" + orderNumber + "]");

        sendDoorayMessage(request);
    }

    //인증번호 메시지
    public void sendVerificationCode(SendMessageRequest request) {
        MemberResponse memberDto = memberService.getMemberByEmail(request.getRecipientEmail());
        if (!memberDto.getStatus().equals(Status.INACTIVE)) {
            throw new IllegalStateException("휴면 회원만 인증이 필요합니다!");
        }

        String code = generateVerificationCode();
        String key = "verify:" + request.getRecipientEmail();

        // Redis에 인증번호 저장 (3분간 유효)
        redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);

        String messageText = request.getRecipientName() + "님 🙌\n" +
                "휴면해제를 위해 아래 인증번호를 입력해주세요.";

        request.setText(messageText);
        request.setAttachmentTitle("🔑인증번호");
        request.setAttachmentText("[" + code + "]" + "\n3분 동안 유효합니다.");
        request.setAttachmentColor("red");
        sendDoorayMessage(request);
    }

    //인증번호 생성
    private String generateVerificationCode() {
        int code = SECURE_RANDOM.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }

}
