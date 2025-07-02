package com.nhnacademy.illuwa.domain.pointpolicy.repo;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.PointPolicy;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Commit;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PointPolicyRepositoryTest {
    @TestConfiguration
    static class TestQueryDslConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Autowired
    PointPolicyRepository pointPolicyRepository;

    @Order(1)
    @Test
    @Commit
    @DisplayName("포인트 정책 등록")
    void testSavePointPolicy(){
        PointPolicy newPolicy = PointPolicy.builder()
                .policyKey("winter_event")
                .value(new BigDecimal("2025"))
                .valueType(PointValueType.AMOUNT)
                .description("2025 겨울 이벤트 적립")
                .build();
        PointPolicy saved = pointPolicyRepository.save(newPolicy);

        assertNotNull(saved);
        assertEquals(new BigDecimal("2025"), saved.getValue());
    }

    @Order(2)
    @Test
    @Commit
    @DisplayName("포인트 정책 조회")
    void testFindPointPolicy(){
        Optional<PointPolicy> optional = pointPolicyRepository.findById("winter_event");

        assertNotNull(optional);

        PointPolicy winterPointPolicy = optional.orElseThrow();
        assertEquals(0,winterPointPolicy.getValue().compareTo(new BigDecimal("2025")));
        assertEquals(PointValueType.AMOUNT, winterPointPolicy.getValueType());
        assertEquals("2025 겨울 이벤트 적립", winterPointPolicy.getDescription());
    }

    @Order(3)
    @Test
    @Commit
    @DisplayName("포인트 정책 수정")
    void testUpdatePointPolicy() {
        Optional<PointPolicy> optional = pointPolicyRepository.findById("winter_event");
        assertTrue(optional.isPresent(), "winter_event 정책이 존재해야 합니다");

        PointPolicy winterPolicy = optional.get();

        winterPolicy.changeValue(new BigDecimal("2026"));
        winterPolicy.changeDescription("새해의 기대를 담아 2026포인트 적립");

        pointPolicyRepository.save(winterPolicy);

        PointPolicy updated = pointPolicyRepository.findById("winter_event").orElseThrow();
        assertEquals(0, updated.getValue().compareTo(new BigDecimal("2026")));
        assertEquals("새해의 기대를 담아 2026포인트 적립", updated.getDescription());
        assertEquals(PointValueType.AMOUNT, updated.getValueType());
    }

    @Order(4)
    @Test
    @Commit
    @DisplayName("포인트 정책 삭제")
    void testDeletePointPolicy() {
        PointPolicy policy = pointPolicyRepository.findById("winter_event").get();
        pointPolicyRepository.deleteById(policy.getPolicyKey());

        assertFalse(pointPolicyRepository.existsById(policy.getPolicyKey()));
    }
}
