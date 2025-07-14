package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.common.testconfig.GradeTestDataConfig;
import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

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

    Member createMember(String name, String email, Grade grade, Role role, Status status) {
        return Member.builder()
                .name(name)
                .birth(LocalDate.of(1990, 1, 1))
                .email(email)
                .password("12345!")
                .role(role)
                .contact("01012345678")
                .grade(grade)
                .point(new BigDecimal("1000"))
                .status(status)
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
        Member member = createMember("카리나", "karina@naver.com", royalGrade, Role.USER, Status.ACTIVE);
        Member saved = memberRepository.save(member);

        assertEquals("카리나", saved.getName());
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void testUpdate() {
        Member member = memberRepository.save(createMember("윈터", "winter@naver.com", basicGrade, Role.USER, Status.ACTIVE));

        member.changeName("윈터수정");
        member.changePoint(new BigDecimal("5000"));
        Member updated = memberRepository.save(member);

        assertEquals("윈터수정", updated.getName());
        assertEquals(new BigDecimal("5000"), updated.getPoint());
    }

    @Test
    @DisplayName("회원 삭제 성공 테스트")
    void testDelete() {
        Member member = memberRepository.save(createMember("닝닝", "ningning@naver.com", royalGrade, Role.USER, Status.ACTIVE));
        Long id = member.getMemberId();

        memberRepository.deleteById(id);
        Optional<Member> found = memberRepository.findById(id);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("특정 등급 회원 조회 테스트")
    void testFindByGrade() {
        int orgRoyalCount = memberRepository.findByGradeName(royalGrade.getGradeName()).size();

        memberRepository.save(createMember("아이유", "iu@naver.com", royalGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("태연", "taeyeon@naver.com", platinumGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("슬기", "seulgi@naver.com", royalGrade, Role.USER, Status.ACTIVE));

        List<Member> royalMembers = memberRepository.findByGradeName(royalGrade.getGradeName());

        assertEquals(orgRoyalCount + 2, royalMembers.size());
        assertTrue(royalMembers.stream().allMatch(m -> m.getGrade().getGradeName() == royalGrade.getGradeName()));
    }

    @Test
    @DisplayName("회원 포인트 조회 테스트")
    void testFindPoint() {
        Member member = memberRepository.save(createMember("지민", "jimin@naver.com", goldGrade, Role.USER, Status.ACTIVE));
        BigDecimal point = memberRepository.findPoint(member.getMemberId());

        assertEquals(0, point.compareTo(new BigDecimal("1000.00")));
    }

    @Test
    @DisplayName("회원 활동 여부 검사 - ACTIVE 일 때 false 리턴")
    void testIsNotActiveMember_Active() {
        Member member = memberRepository.save(createMember("태용", "taeyong@naver.com", basicGrade, Role.USER, Status.ACTIVE));
        boolean result = memberRepository.isNotActiveMember(member.getMemberId());
        assertFalse(result); // ACTIVE 이므로 false 리턴
    }

    @Test
    @DisplayName("회원 활동 여부 검사 - INACTIVE 일 때 true 리턴")
    void testIsNotActiveMember_NotActive() {
        Member member = memberRepository.save(createMember("마크", "mark@naver.com", basicGrade, Role.USER, Status.INACTIVE));
        boolean result = memberRepository.isNotActiveMember(member.getMemberId());
        assertTrue(result);
    }

    @Test
    @DisplayName("회원 활동 여부 검사 - 없는 회원일 때 true 리턴")
    void testIsNotActiveMember_NoMember() {
        boolean result = memberRepository.isNotActiveMember(-1L);
        assertTrue(result);
    }

    @Test
    @DisplayName("회원 마지막 로그인 내림차순 페이징 조회 테스트")
    void testFindMemberOrderByLastLoginAtOrderDesc() {
        memberRepository.save(createMember("은하", "eunha@naver.com", goldGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("하니", "hani@naver.com", goldGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("관리자", "admin@naver.com", basicGrade, Role.ADMIN, Status.ACTIVE)); // 관리자 포함해서 필터링 테스트

        var page = memberRepository.findMemberOrderByLastLoginAtOrderDesc(org.springframework.data.domain.PageRequest.of(0, 10));

        assertTrue(page.getContent().stream().noneMatch(m -> m.getRole() == Role.ADMIN));
        assertFalse(page.getContent().isEmpty());
    }

    @Test
    @DisplayName("특정 등급 회원 마지막 로그인 내림차순 페이징 조회 테스트")
    void testFindMemberByGradeNameOrderByLastLoginAtOrderDesc() {
        memberRepository.save(createMember("세정", "sejeong@naver.com", royalGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("예린", "yerin@naver.com", royalGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("유주", "yujoo@naver.com", platinumGrade, Role.USER, Status.ACTIVE));
        memberRepository.save(createMember("관리자", "admin2@naver.com", royalGrade, Role.ADMIN, Status.ACTIVE)); // 관리자 포함해서 필터링 테스트

        var page = memberRepository.findMemberByGradeNameOrderByLastLoginAtOrderDesc(royalGrade.getGradeName(), org.springframework.data.domain.PageRequest.of(0, 10));

        assertTrue(page.getContent().stream().noneMatch(m -> m.getRole() == Role.ADMIN));
        assertTrue(page.getContent().stream().allMatch(m -> m.getGrade().getGradeName() == royalGrade.getGradeName()));
    }
}
