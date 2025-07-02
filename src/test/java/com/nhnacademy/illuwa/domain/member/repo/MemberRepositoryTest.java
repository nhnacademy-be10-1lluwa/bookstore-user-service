package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.common.testconfig.GradeTestDataConfig;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@DataJpaTest
@Import({MemberRepositoryImpl.class, GradeTestDataConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @TestConfiguration
    static class QueryDslTestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Autowired
    GradeTestDataConfig gradeData;

    @Autowired
    MemberRepository memberRepository;

    Grade basicGrade;
    Grade goldGrade;
    Grade royalGrade;
    Grade platinumGrade;

    Member createMember(String name, String email, Grade grade, Role role) {
        return Member.builder()
                .name(name)
                .birth(LocalDate.of(1990, 1, 1))
                .email(email)
                .password("12345!")
                .role(role)
                .contact("01012345678")
                .grade(grade)
                .point(new BigDecimal("1000"))
                .status(Status.ACTIVE)
                .build();
    }

    @BeforeAll
    void beforeAll() {
        basicGrade = gradeData.getBasicGrade();
        goldGrade = gradeData.getGoldGrade();
        royalGrade = gradeData.getRoyalGrade();
        platinumGrade = gradeData.getPlatinumGrade();
    }

    @Test
    @DisplayName("회원 저장 성공 테스트")
    void testSave() {
        Member member = createMember("카리나", "karina@naver.com",royalGrade, Role.USER);
        Member saved = memberRepository.save(member);

        assertEquals("카리나", saved.getName());
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void testUpdate() {
        Member member = memberRepository.save(createMember("윈터", "winter@naver.com", basicGrade, Role.USER));

        member.changeName("윈터수정");
        member.changePoint(new BigDecimal("5000"));
        Member updated = memberRepository.save(member);

        assertEquals("윈터수정", updated.getName());
        assertEquals(new BigDecimal("5000"), updated.getPoint());
    }

    @Test
    @DisplayName("회원 삭제 성공 테스트")
    void testDelete() {
        Member member = memberRepository.save(createMember("닝닝", "ningning@naver.com", royalGrade, Role.USER));
        Long id = member.getMemberId();

        memberRepository.deleteById(id);
        Optional<Member> found = memberRepository.findById(id);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("특정 등급 회원 조회 테스트")
    void testFindByGrade() {
        memberRepository.save(createMember("아이유", "iu@naver.com", royalGrade, Role.USER));
        memberRepository.save(createMember("태연", "taeyeon@naver.com", platinumGrade, Role.USER));
        memberRepository.save(createMember("슬기", "seulgi@naver.com", royalGrade, Role.USER));

        List<Member> royalMembers = memberRepository.findByGrade(royalGrade);

        assertEquals(2, royalMembers.size());
        assertTrue(royalMembers.stream().allMatch(m -> m.getGrade() == royalGrade));
    }

    @Test
    @DisplayName("특정 역할 회원 조회 테스트")
    void testFindByRole() {
        memberRepository.save(createMember("보아", "boa@naver.com", basicGrade, Role.ADMIN));
        memberRepository.save(createMember("제니", "jennie@naver.com", basicGrade, Role.ADMIN));
        memberRepository.save(createMember("지수", "jisoo@naver.com", basicGrade, Role.USER));

        List<Member> admins = memberRepository.findByRole(Role.ADMIN);

        assertEquals(2, admins.size());
        assertTrue(admins.stream().allMatch(m -> m.getRole() == Role.ADMIN));
    }
}
