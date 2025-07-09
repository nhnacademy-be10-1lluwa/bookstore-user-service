package com.nhnacademy.illuwa.common.config.init;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.repo.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(2)
public class PointPolicyInitializer implements ApplicationRunner {

    private final PointPolicyRepository pointPolicyRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        List<PointPolicy> initPolicies = List.of(
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
                        .value(new BigDecimal("0.01")) // 0.5% 변경 예정
                        .valueType(PointValueType.RATE)
                        .description("도서구매 기본 적립률")
                        .build()
        );

        for (PointPolicy policy : initPolicies) {
            pointPolicyRepository.findById(policy.getPolicyKey())
                    .ifPresentOrElse(
                            existing -> {
                                boolean needsUpdate =
                                        existing.getValueType() != policy.getValueType() ||
                                                !existing.getStatus().equals(policy.getStatus()) ||
                                                existing.getValue().compareTo(policy.getValue()) != 0 ||
                                                !existing.getDescription().equals(policy.getDescription());

                                if (needsUpdate) {
                                    existing.changeValue(policy.getValue());
                                    existing.changeStatus(policy.getStatus());
                                    existing.changeValueType(policy.getValueType());
                                    existing.changeDescription(policy.getDescription());
                                    pointPolicyRepository.save(existing);
                                }
                            },
                            () -> pointPolicyRepository.save(policy)
                    );
        }
    }
}