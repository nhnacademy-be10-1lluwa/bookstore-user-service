package com.nhnacademy.illuwa.domain.message.service;

import com.nhnacademy.illuwa.domain.message.dto.InactiveVerificationRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SendMessageService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.dooray.webhook.url}")
    private String doorayWebhookUrl;

    public void sendMessage(SendMessageRequest request) {
        sendToDooray("테스트", request.getText());
    }

    public void sendVerificationNumber(InactiveVerificationRequest request) {
        String code = generateVerificationCode();
        String key = "verify:" + request.getMemberId();

        // Redis에 인증번호 저장 (3분간)
        redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);

        // 인증번호 포함 메시지 전송
        String content = "[1lluwa] " + request.getContent() + "\n인증번호: " + code;
        sendToDooray("1lluwa", content);
    }

    private void sendToDooray(String botName, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("botName", botName);
        body.put("text", content);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    doorayWebhookUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            System.out.println("두레이 메시지 전송 성공! 응답: " + response.getBody());
        } catch (Exception e) {
            System.err.println("두레이 메시지 전송 실패: " + e.getMessage());
        }
    }

    private String generateVerificationCode() {
        int code = 100_000 + new Random().nextInt(900_000);
        return String.valueOf(code);
    }
}
