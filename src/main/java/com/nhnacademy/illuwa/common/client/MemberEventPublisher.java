package com.nhnacademy.illuwa.common.client;

import com.nhnacademy.illuwa.domain.member.dto.MemberEventDto;
import com.nhnacademy.illuwa.domain.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemberEventPublisher {

    // RabbitMQ로 메시지를 보내기 위한 템플릿(도구) 객체를 선언
    private final RabbitTemplate rabbitTemplate;

    public MemberEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMemberCreateEvent(MemberEventDto message) {
        rabbitTemplate.convertAndSend("1lluwa_welcome_queue",message);
        log.info(message.getName());
        log.info(message.getMemberId().toString());
        log.info(message.getBirth().toString());
        log.info("회원가입 이벤트 발송 성공");
    }
}