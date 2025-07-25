package com.nhnacademy.illuwa.domain.grade.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByGradeName(GradeName gradeName);
    Optional<Grade> findByGradeName(GradeName gradeName);
}
