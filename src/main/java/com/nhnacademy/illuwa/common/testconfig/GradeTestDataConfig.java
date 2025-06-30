package com.nhnacademy.illuwa.common.testconfig;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Getter
public class GradeTestDataConfig {

    private final GradeRepository gradeRepository;

    private Grade basicGrade;
    private Grade goldGrade;
    private Grade royalGrade;
    private Grade platinumGrade;

    @PostConstruct
    public void init() {
        if (!gradeRepository.existsByGradeName(GradeName.BASIC)) {
            gradeRepository.save(Grade.builder()
                    .gradeName(GradeName.BASIC)
                    .priority(4)
                    .pointRate(BigDecimal.valueOf(0.01))
                    .minAmount(BigDecimal.ZERO)
                    .maxAmount(BigDecimal.valueOf(100_000))
                    .build());
        }

        if (!gradeRepository.existsByGradeName(GradeName.GOLD)) {
            gradeRepository.save(Grade.builder()
                    .gradeName(GradeName.GOLD)
                    .priority(3)
                    .pointRate(BigDecimal.valueOf(0.02))
                    .minAmount(BigDecimal.valueOf(100_000))
                    .maxAmount(BigDecimal.valueOf(200_000))
                    .build());
        }

        if (!gradeRepository.existsByGradeName(GradeName.ROYAL)) {
            gradeRepository.save(Grade.builder()
                    .gradeName(GradeName.ROYAL)
                    .priority(2)
                    .pointRate(BigDecimal.valueOf(0.025))
                    .minAmount(BigDecimal.valueOf(200_000))
                    .maxAmount(BigDecimal.valueOf(300_000))
                    .build());
        }

        if (!gradeRepository.existsByGradeName(GradeName.PLATINUM)) {
            gradeRepository.save(Grade.builder()
                    .gradeName(GradeName.PLATINUM)
                    .priority(1)
                    .pointRate(BigDecimal.valueOf(0.03))
                    .minAmount(BigDecimal.valueOf(300_000))
                    .maxAmount(null)
                    .build());
        }

        basicGrade = gradeRepository.findByGradeName(GradeName.BASIC).orElseThrow();
        goldGrade = gradeRepository.findByGradeName(GradeName.GOLD).orElseThrow();
        royalGrade = gradeRepository.findByGradeName(GradeName.ROYAL).orElseThrow();
        platinumGrade = gradeRepository.findByGradeName(GradeName.PLATINUM).orElseThrow();
    }
}