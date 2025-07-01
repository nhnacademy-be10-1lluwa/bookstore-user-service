package com.nhnacademy.illuwa.common.config.init;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class GradeInitializer {

    private final GradeRepository gradeRepository;

    @PostConstruct
    public void init() {
        // 이미 등급 데이터가 있으면 중복 삽입 방지
        if (gradeRepository.count() > 0) {
            return;
        }

        List<Grade> grades = List.of(
                Grade.builder()
                        .gradeName(GradeName.BASIC)
                        .priority(4)
                        .pointRate(BigDecimal.valueOf(0.01))
                        .minAmount(BigDecimal.valueOf(0))
                        .maxAmount(BigDecimal.valueOf(100_000))
                        .build(),

                Grade.builder()
                        .gradeName(GradeName.GOLD)
                        .priority(3)
                        .pointRate(BigDecimal.valueOf(0.02))
                        .minAmount(BigDecimal.valueOf(100_000))
                        .maxAmount(BigDecimal.valueOf(200_000))
                        .build(),

                Grade.builder()
                        .gradeName(GradeName.ROYAL)
                        .priority(2)
                        .pointRate(BigDecimal.valueOf(0.025))
                        .minAmount(BigDecimal.valueOf(200_000))
                        .maxAmount(BigDecimal.valueOf(300_000))
                        .build(),

                Grade.builder()
                        .gradeName(GradeName.PLATINUM)
                        .priority(1)
                        .pointRate(BigDecimal.valueOf(0.03))
                        .minAmount(BigDecimal.valueOf(300_000))
                        .maxAmount(null)
                        .build()
        );

        gradeRepository.saveAll(grades);
    }
}
