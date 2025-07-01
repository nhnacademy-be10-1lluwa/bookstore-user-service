package com.nhnacademy.illuwa.common.config.init;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(3)
public class PointPolicyInitializer implements ApplicationRunner {

    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public void run(ApplicationArguments args){
        if(pointPolicyRepository.count() > 0) return;

        List<PointPolicy> policies = List.of(
                PointPolicy.builder()
                        .policyKey("join_point")
                        .value(new BigDecimal("5000"))
                        .valueType(PointValueType.AMOUNT)
                        .description("회원가입 포인트 적립액")
                        .build(),

                PointPolicy.builder()
                    .policyKey("review_point")
                    .value(new BigDecimal("200"))
                    .valueType(PointValueType.AMOUNT)
                    .description("리뷰 포인트 적립액")
                    .build(),

                PointPolicy.builder()
                    .policyKey("photo_review_point")
                    .value(new BigDecimal("500"))
                    .valueType(PointValueType.AMOUNT)
                    .description("포토리뷰 포인트 적립액")
                    .build(),

                PointPolicy.builder()
                    .policyKey("book_default_rate")
                    .value(new BigDecimal("1.00"))
                    .valueType(PointValueType.RATE)
                    .description("도서구매 기본 적립률")
                    .build()
        );
        pointPolicyRepository.saveAll(policies);
    }
}
