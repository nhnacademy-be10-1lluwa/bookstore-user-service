package com.nhnacademy.illuwa.domain.grade.service;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.exception.GradeNotFoundException;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;

    public Grade getByGradeName(GradeName gradeName){
        return gradeRepository.findByGradeName(gradeName).orElseThrow(()-> new GradeNotFoundException(gradeName.toString()));
    }

    public Grade calculateGrade(BigDecimal netOrderAmount) {
        return gradeRepository.findAll(Sort.by("priority")).stream()
                .filter(g -> g.inRange(netOrderAmount))
                .findFirst()
                .orElseThrow(() -> new GradeNotFoundException(netOrderAmount));
    }
}
