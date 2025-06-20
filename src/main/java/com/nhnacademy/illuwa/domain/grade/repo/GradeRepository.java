package com.nhnacademy.illuwa.domain.grade.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {  //pk 우선 Long
    Optional<Grade> findByGradeName(String gradeName);
}
