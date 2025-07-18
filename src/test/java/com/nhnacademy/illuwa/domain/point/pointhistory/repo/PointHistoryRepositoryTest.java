package com.nhnacademy.illuwa.domain.point.pointhistory.repo;

import com.nhnacademy.illuwa.common.testconfig.GradeTestDataConfig;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PointHistoryRepositoryImpl.class, GradeTestDataConfig.class})
class PointHistoryRepositoryTest {

    @TestConfiguration
    static class TestQueryDslConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Autowired
    GradeTestDataConfig gradeData;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    Member member;

    @BeforeEach
    void setUp(){
        Grade basicGrade = gradeData.getBasicGrade();

        member = Member.builder()
                .name("카리나")
                .birth(LocalDate.of(2002,2,19))
                .email("gongju@illuwa.com")
                .password("Pwd12345678!*")
                .contact("010-1111-2222")
                .grade(basicGrade)
                .build();

        member = memberRepository.save(member);

        PointHistory history1 = PointHistory.builder()
                .memberId(member.getMemberId())
                .reason(PointReason.JOIN)
                .type(PointHistoryType.EARN)
                .amount(new BigDecimal("5000"))
                .balance(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        PointHistory history2 = PointHistory.builder()
                .memberId(member.getMemberId())
                .reason(PointReason.PURCHASE)
                .type(PointHistoryType.DEDUCT)
                .amount(new BigDecimal("300"))
                .balance(new BigDecimal("4700"))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        PointHistory history3 = PointHistory.builder()
                .memberId(member.getMemberId())
                .reason(PointReason.PHOTO_REVIEW)
                .type(PointHistoryType.EARN)
                .amount(new BigDecimal("500"))
                .balance(new BigDecimal("4200"))
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history1);
        pointHistoryRepository.save(history2);
        pointHistoryRepository.save(history3);
    }

    @Test
    @DisplayName("포인트 히스토리 저장 및 ID로 조회")
    void testSaveAndFindById() {
        PointHistory history = PointHistory.builder()
                .amount(new BigDecimal("5000"))
                .type(PointHistoryType.EARN)
                .reason(PointReason.JOIN)
                .balance(new BigDecimal("5000"))
                .createdAt(LocalDateTime.now())
                .memberId(member.getMemberId())
                .build();

        PointHistory saved = pointHistoryRepository.save(history);

        Optional<PointHistory> found = pointHistoryRepository.findById(saved.getPointHistoryId());
        assertThat(found).isPresent();
        assertThat(found.get().getType()).isEqualTo(PointHistoryType.EARN);
        assertThat(found.get().getMemberId()).isEqualTo(member.getMemberId());
    }

    @Test
    @DisplayName("모든 포인트 히스토리 조회")
    void testFindAll() {
        List<PointHistory> orgHistory = pointHistoryRepository.findAll();

        PointHistory history1 = new PointHistory(new BigDecimal("200"), PointReason.REVIEW, PointHistoryType.EARN, new BigDecimal("200"), LocalDateTime.of(2025,3,19,21,29), member.getMemberId());
        PointHistory history2 = new PointHistory(new BigDecimal("500"), PointReason.PHOTO_REVIEW, PointHistoryType.EARN, new BigDecimal(800), LocalDateTime.now(), member.getMemberId());

        pointHistoryRepository.save(history1);
        pointHistoryRepository.save(history2);

        List<PointHistory> nowHistory = pointHistoryRepository.findAll();

        assertThat(orgHistory.size()+2).isEqualTo(nowHistory.size());
    }

    @Test
    @DisplayName("member 포인트 히스토리 - 최신순")
    void testFindByMemberIdOrderByCreatedAtDesc() {
        List<PointHistory> histories = pointHistoryRepository.findByMemberIdOrderByCreatedAtDesc(member.getMemberId());

        assertThat(histories).hasSize(3);
        assertThat(histories.get(0).getCreatedAt()).isAfter(histories.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("EARN 내역 조회")
    void testFindByPointTypeEarn() {
        List<PointHistory> earnList = pointHistoryRepository.findByPointTypeEarn(member.getMemberId());

        assertThat(earnList).hasSize(2);
        assertThat(earnList).allMatch(p -> p.getType() == PointHistoryType.EARN);
    }

    @Test
    @DisplayName("USE 내역 조회")
    void testFindByPointTypeUse() {
        List<PointHistory> useList = pointHistoryRepository.findByPointTypeUse(member.getMemberId());

        assertThat(useList).hasSize(1);
        assertThat(useList.getFirst().getType()).isEqualTo(PointHistoryType.DEDUCT);
    }

    @Test
    @DisplayName("날짜 범위 내 내역 조회")
    void testFindByDate() {
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();

        List<PointHistory> list = pointHistoryRepository.findByDate(member.getMemberId(), startDate, endDate);

        assertThat(list).hasSize(3);
    }
}
