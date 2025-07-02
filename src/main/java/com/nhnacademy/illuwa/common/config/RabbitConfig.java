package com.nhnacademy.illuwa.common.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /**
     * "test_welcome_queue" -> 이름의 큐를 생성
     * durable = true -> 해당 큐에 내구성(ture) 설정 시 서버 재시작 시에도 살아 있음
     */
    @Bean
    public Queue memberRegisterQueue() {
        return new Queue("test_welcome_queue", true);
    }


    /**
     * Jackson 라이브러리 사용
     * 자바 객체 -> JSON -> 자바 객체 변환을 담당하는 메시지 변환기(MessageConverter) 빈 등록
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 메시지를 보내고 받는데 쓰이는 핵심 빈(RabbitTemplate)생성
     * 생성 시 -> RabbitMQ 서버와 연결하는 'ConnectionFactory'를 주입받음
     * 여기서 메시지 변환기가 위쪽에 만든 JSON 변환기로 변환
     * 해당 설정으로 인해 rabbitTemplate.convertAndSend(Object payload) 시
     * 내부적으로 JSON이 직렬화가 수행되면서 POJO를 바로 메시지로 보냄
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}