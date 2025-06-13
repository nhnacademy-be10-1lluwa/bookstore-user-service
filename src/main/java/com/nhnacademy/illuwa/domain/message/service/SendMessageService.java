package com.nhnacademy.illuwa.domain.message.service;

<<<<<<< feature/13-InactiveVerification-service
import com.nhnacademy.illuwa.domain.message.dto.SendVerificationCodeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
=======
import com.nhnacademy.illuwa.domain.message.dto.InactiveVerificationRequest;
import com.nhnacademy.illuwa.domain.message.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
>>>>>>> develop
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

<<<<<<< feature/13-InactiveVerification-service
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
=======
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

>>>>>>> develop
@Service
@RequiredArgsConstructor
public class SendMessageService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.dooray.webhook.url}")
    private String doorayWebhookUrl;

<<<<<<< feature/13-InactiveVerification-service

    public void sendVerificationNumber(SendVerificationCodeRequest request) {
        String code = generateVerificationCode();
        String key = "verify:" + request.getEmail();

        // Redis에 인증번호 저장 (3분간)
        redisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES);
        String text = request.getEmail() + "님 🙌\n" +
                "휴면해제를 위해 화면에 인증번호를 입력해주세요.";
        request.setText(text);
        request.setAttachContent(new AbstractMap.SimpleEntry<>("🔑인증번호", code));
        sendToDooray("1lluwa", request);
    }

    private void sendToDooray(String botName, SendVerificationCodeRequest request) {
=======
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
>>>>>>> develop
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("botName", botName);
<<<<<<< feature/13-InactiveVerification-service
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
=======
        body.put("text", content);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
>>>>>>> develop

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    doorayWebhookUrl,
                    HttpMethod.POST,
<<<<<<< feature/13-InactiveVerification-service
                    doorayRequest,
                    String.class
            );
            log.debug("두레이 메시지 전송 성공! 응답: {}", response.getBody());
        } catch (Exception e) {
            log.error("두레이 메시지 전송 실패: {}", e.getMessage());
=======
                    request,
                    String.class
            );
            System.out.println("두레이 메시지 전송 성공! 응답: " + response.getBody());
        } catch (Exception e) {
            System.err.println("두레이 메시지 전송 실패: " + e.getMessage());
>>>>>>> develop
        }
    }

    private String generateVerificationCode() {
        int code = 100_000 + new Random().nextInt(900_000);
        return String.valueOf(code);
    }
}
