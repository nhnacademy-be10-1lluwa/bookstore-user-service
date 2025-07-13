package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.common.client.DoorayMessageClient;
import com.nhnacademy.illuwa.common.exception.ActionNotAllowedException;
import com.nhnacademy.illuwa.domain.guest.dto.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.member.dto.InactiveCheckResponse;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageResponse;
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

    public SendMessageResponse sendDoorayMessage(SendMessageRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("botName", request.getBotName());
        body.put("botIconImage", "/static/icon/1lluwa-favicon.png");
        if(request.getText() != null){
            body.put("text", request.getText());
        }

        if(request.hasAttachment()){
            Map<String, Object> attachment = new HashMap<>();

            attachment.put("title", request.getAttachmentTitle());
            attachment.put("titleLink", request.getAttachmentTitleLink());
            attachment.put("text", request.getAttachmentText());
            attachment.put("color", request.getAttachmentColor());

            body.put("attachments", List.of(attachment));
        }
        try {
            doorayMessageClient.sendMessage(body);
            String message = "메시지 전송이 완료되었습니다!";
            log.debug(message);
            return new SendMessageResponse(true, request.getRecipientEmail(), message, request.getText());
        } catch (Exception e) {
            String message = "메시지 전송이 실패했습니다.";
            log.error(message + "{}", e.getMessage());
            return new SendMessageResponse(false, request.getRecipientEmail(), message, null);
        }
    }

    //비회원 주문완료 메시지
    public SendMessageResponse sendOrderMessage(GuestOrderRequest guestOrderRequest) {
        SendMessageRequest request = new SendMessageRequest();
        request.setText(request.getRecipientName() + "님의 소중한 주문이 완료되었습니다!😎");
        request.setAttachmentTitle("🎁주문완료");
        request.setAttachmentText("주문번호: " + "[" + guestOrderRequest.getOrderNumber() + "]");
        return sendDoorayMessage(request);
    }

    //인증번호 메시지
    public SendMessageResponse sendVerificationCode(SendMessageRequest request) {
        InactiveCheckResponse inactiveMemberInfo = memberService.getInactiveMemberInfoByEmail(request.getRecipientEmail());
        if (!inactiveMemberInfo.getStatus().equals(Status.INACTIVE)) {
            throw new ActionNotAllowedException("휴면 회원만 인증이 필요합니다!");
        }
        String code = generateVerificationCode();
        String key = "verify:" + request.getRecipientEmail();

        redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);

        String messageText = request.getRecipientName() + "님 🙌\n" +
                "3분이내에 아래 인증번호를 입력해주세요.";

        request.setText(messageText);
        request.setAttachmentTitle("🔑인증번호");
        request.setAttachmentTitleLink("https://book1lluwa.store/");
        request.setAttachmentText("[" + code + "]");
        request.setAttachmentColor("orange");
        return sendDoorayMessage(request);
    }

    //인증번호 생성
    public String generateVerificationCode() {
        int code = SECURE_RANDOM.nextInt(900_000) + 100_000;
        return String.valueOf(code);
    }
}
