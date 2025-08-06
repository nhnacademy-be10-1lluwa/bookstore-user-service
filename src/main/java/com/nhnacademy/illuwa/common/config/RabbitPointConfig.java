package com.nhnacademy.illuwa.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitPointConfig {
    public static final String POINT_EXCHANGE = "point.exchange";
    public static final String POINT_QUEUE = "point.used.queue";
    public static final String POINT_ROUTING_KEY = "point.used";

    @Bean
    public TopicExchange pointExchange() {
        return new TopicExchange(POINT_EXCHANGE);
    }

    @Bean
    public Queue pointUsedQueue() {
        return new Queue(POINT_QUEUE);
    }

    @Bean
    public Binding pointBinding() {
        return BindingBuilder.bind(pointUsedQueue()).to(pointExchange()).with(POINT_ROUTING_KEY);
    }

    @Bean(name = "pointJacksonConverter")
    public Jackson2JsonMessageConverter pointJacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "pointListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory pointListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("pointJacksonConverter") Jackson2JsonMessageConverter pointJacksonConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(pointJacksonConverter);
        return factory;
    }
}
