package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.domain.message.dto.InactiveVerificationRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.dooray.webhook.url}")
    private String doorayWebhookUrl;

    public void sendMessage(SendMessageRequest request) {
        sendToDooray("알림봇", request);
    }

    public void sendVerificationNumber(InactiveVerificationRequest request) {
        String code = generateVerificationCode();
        String key = "verify:" + request.getEmail();

        // Redis에 인증번호 저장 (3분간)
        redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);
        String text = request.getEmail() + "님 🙌\n" +
                "휴면해제를 위해 화면에 인증번호를 입력해주세요.";
        SendMessageRequest messageRequest = new SendMessageRequest(text, new AbstractMap.SimpleEntry<>("🔑인증번호", code));
        sendToDooray("1lluwa", messageRequest);
    }

    private void sendToDooray(String botName, SendMessageRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("botName", botName);
        body.put("text", request.getText());

        Map.Entry<String, Object> content = request.getAttachContent();
        if(content != null){
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("title", content.getKey());
            attachment.put("text", "["+ content.getValue()+ "]");
            attachment.put("color", "red");

            body.put("attachments", List.of(attachment));
        }

        HttpEntity<Map<String, Object>> doorayRequest = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    doorayWebhookUrl,
                    HttpMethod.POST,
                    doorayRequest,
                    String.class
            );
            log.debug("두레이 메시지 전송 성공! 응답: {}", response.getBody());
        } catch (Exception e) {
            log.error("두레이 메시지 전송 실패: {}", e.getMessage());
        }
    }

    private String generateVerificationCode() {
        int code = 100_000 + new Random().nextInt(900_000);
        return String.valueOf(code);
    }
}
