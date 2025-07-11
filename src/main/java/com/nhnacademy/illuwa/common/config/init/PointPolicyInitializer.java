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

@Component
@RequiredArgsConstructor
@Order(2)
public class PointPolicyInitializer implements ApplicationRunner {

    private final PointPolicyRepository pointPolicyRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        saveOrUpdate("join_point", new BigDecimal("5000"), PointValueType.AMOUNT, "회원가입 포인트 적립액");
        saveOrUpdate("review_point", new BigDecimal("200"), PointValueType.AMOUNT, "리뷰 포인트 적립액");
        saveOrUpdate("photo_review_point", new BigDecimal("500"), PointValueType.AMOUNT, "포토리뷰 포인트 적립액");
        saveOrUpdate("book_default_rate", new BigDecimal("0.01"), PointValueType.RATE, "도서구매 기본 적립률");
    }

    private void saveOrUpdate(String key, BigDecimal value, PointValueType type, String description) {
        PointPolicy existing = pointPolicyRepository.findById(key).orElse(null);

        if (existing == null) {
            pointPolicyRepository.save(PointPolicy.builder()
                    .policyKey(key)
                    .value(value)
                    .valueType(type)
                    .description(description)
                    .build());
        } else {
            // 변경사항이 있는 경우에만 업데이트
            boolean changed = !existing.getValue().equals(value)
                    || !existing.getValueType().equals(type)
                    || !existing.getDescription().equals(description);
            if (changed) {
                existing.changeValue(value);
                existing.changeValueType(type);
                existing.changeDescription(description);
                pointPolicyRepository.save(existing);
            }
        }
    }
}