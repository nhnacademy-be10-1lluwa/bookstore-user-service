package com.nhnacademy.illuwa.domain.point.consumer;

import com.nhnacademy.illuwa.common.config.RabbitPointConfig;
import com.nhnacademy.illuwa.domain.point.dto.PointSavedEvent;
import com.nhnacademy.illuwa.domain.point.dto.PointUsedEvent;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.point.utils.PointManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventConsumer {

    private final PointManager pointManager;

    @RabbitListener(queues = RabbitPointConfig.USED_QUEUE,
            containerFactory = "pointListenerContainerFactory")
    public void handlePointUsedEvent(PointUsedEvent event) {
        log.info("📨 PointUsedEvent 수신 - memberId={}, usedPoint={}", event.getMemberId(), event.getUsedPoint());

        UsedPointRequest request = UsedPointRequest.builder()
                        .memberId(event.getMemberId())
                        .usedPoint(event.getUsedPoint())
                        .build();

        pointManager.processUsedPoint(request);
    }

    @RabbitListener(queues = RabbitPointConfig.SAVED_QUEUE,
            containerFactory = "pointListenerContainerFactory")
    public void handlePointSavedEvent(PointSavedEvent event) {
        log.info("📨 PointSavedEvent 수신 - memberId={}, price={}", event.getMemberId(), event.getPrice());

        PointAfterOrderRequest request = PointAfterOrderRequest.builder()
                .memberId(event.getMemberId())
                .price(event.getPrice())
                .build();

        pointManager.processOrderPoint(request);
    }
}
