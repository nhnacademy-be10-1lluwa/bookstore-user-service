package com.nhnacademy.illuwa.domain.grade.repo;

import com.nhnacademy.illuwa.common.config.JPAConfig;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(JPAConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GradeRepositoryTest {
    @Autowired
    GradeRepository gradeRepository;

    @Test
    @DisplayName("등급이름으로 존재여부 조회")
    void testIsExistsByGradeName(){
        boolean result = gradeRepository.existsByGradeName(GradeName.BASIC);
        assertTrue(result);
    }

    @Test
    @DisplayName("등급이름으로 등급 조회")
    void testFindByGradeName(){
        Grade basicGrade = gradeRepository.findByGradeName(GradeName.BASIC).orElseThrow();
        assertNotNull(basicGrade);
    }

}