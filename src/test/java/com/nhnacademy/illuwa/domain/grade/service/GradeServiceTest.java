package com.nhnacademy.illuwa.domain.grade.service;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.exception.GradeNotFoundException;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {
    @Mock
    GradeRepository gradeRepository;

    @InjectMocks
    GradeService gradeService;

    @Test
    @DisplayName("등급명으로 조회")
    void testGetByGradeName(){
        Grade basicGrade = Grade.builder()
                .gradeName(GradeName.BASIC)
                .build();
        when(gradeRepository.findByGradeName(GradeName.BASIC)).thenReturn(Optional.ofNullable(basicGrade));
        assertEquals(basicGrade, gradeService.getByGradeName(GradeName.BASIC));
    }

    @Test
    @DisplayName("주문금액에 따른 등급 산정")
    void testCalculateGrade(){
        BigDecimal netOrderAmount = new BigDecimal("150000");

        Grade basicGrade = mock(Grade.class);
        Grade goldGrade = mock(Grade.class);

        when(basicGrade.inRange(netOrderAmount)).thenReturn(false);
        when(goldGrade.inRange(netOrderAmount)).thenReturn(true);

        when(gradeRepository.findAll(Sort.by("priority")))
                .thenReturn(List.of(basicGrade, goldGrade));
        Grade result = gradeService.calculateGrade(netOrderAmount);

        assertEquals(goldGrade, result);
    }

    @Test
    @DisplayName("등급 계산 실패 - 조건을 만족하는 등급이 없을 때")
    void testCalculateGradeFail() {
        // given
        BigDecimal netOrderAmount = new BigDecimal("100000");

        Grade anyGrade = mock(Grade.class);
        when(anyGrade.inRange(netOrderAmount)).thenReturn(false);

        when(gradeRepository.findAll(Sort.by("priority")))
                .thenReturn(List.of(anyGrade));

        // when & then
        assertThrows(GradeNotFoundException.class, () ->
                gradeService.calculateGrade(netOrderAmount));
    }
}