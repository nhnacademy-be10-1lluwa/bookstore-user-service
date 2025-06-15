package com.nhnacademy.illuwa.domain.point.entity.pointpolicy.repo;

import com.nhnacademy.illuwa.domain.point.entity.pointpolicy.PointPolicy;
import com.nhnacademy.illuwa.domain.point.entity.pointpolicy.enums.PointValueType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class PointPolicyRepositoryTest {
    @Autowired
    PointPolicyRepository pointPolicyRepository;

    @Order(1)
    @Test
    @Commit
    @DisplayName("포인트 정책 등록")
    void testSavePointPolicy(){
        PointPolicy joinPoint = PointPolicy.builder()
                .policyKey("join_point")
                .value(new BigDecimal("5000"))
                .valueType(PointValueType.AMOUNT)
                .description("회원가입 포인트 적립액")
                .build();

        PointPolicy reviewPoint = PointPolicy.builder()
                .policyKey("review_point")
                .value(new BigDecimal("200"))
                .valueType(PointValueType.AMOUNT)
                .description("리뷰 포인트 적립액")
                .build();

        PointPolicy photoReviewPoint = PointPolicy.builder()
                .policyKey("photo_review_point")
                .value(new BigDecimal("500"))
                .valueType(PointValueType.AMOUNT)
                .description("포토리뷰 포인트 적립액")
                .build();

        PointPolicy bookDefaultRate = PointPolicy.builder()
                .policyKey("book_default_rate")
                .value(new BigDecimal("1.00"))
                .valueType(PointValueType.RATE)
                .description("도서구매 기본 적립률")
                .build();

        pointPolicyRepository.save(joinPoint);
        pointPolicyRepository.save(reviewPoint);
        pointPolicyRepository.save(photoReviewPoint);
        pointPolicyRepository.save(bookDefaultRate);

        assertEquals(4, Arrays.stream(pointPolicyRepository.findAll().toArray()).count());
    }

    @Order(2)
    @Test
    @Commit
    @DisplayName("포인트 정책 조회")
    void testFindPointPolicy(){
        Optional<PointPolicy> joinPointPolicy = pointPolicyRepository.findById("join_point");

        assertNotNull(joinPointPolicy);

        PointPolicy joinPoint = joinPointPolicy.get();
        assertEquals(new BigDecimal("5000"), joinPoint.getValue());
        assertEquals(PointValueType.AMOUNT, joinPoint.getValueType());
        assertEquals("회원가입 포인트 적립액", joinPoint.getDescription());
    }

    @Order(3)
    @Test
    @Commit
    @DisplayName("포인트 정책 수정")
    void testUpdatePointPolicy() {
        Optional<PointPolicy> optional = pointPolicyRepository.findById("join_point");
        assertTrue(optional.isPresent(), "join_point 정책이 존재해야 합니다");

        PointPolicy joinPoint = optional.get();

        joinPoint.setValue(new BigDecimal("6000"));
        joinPoint.setDescription("회원가입 포인트 적립액 수정됨");

        pointPolicyRepository.save(joinPoint);

        PointPolicy updated = pointPolicyRepository.findById("join_point").orElseThrow();
        assertEquals(new BigDecimal("6000"), updated.getValue());
        assertEquals("회원가입 포인트 적립액 수정됨", updated.getDescription());
        assertEquals(PointValueType.AMOUNT, updated.getValueType());
    }

}
