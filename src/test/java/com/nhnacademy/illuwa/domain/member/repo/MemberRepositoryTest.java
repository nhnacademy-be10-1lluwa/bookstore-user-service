package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.repo.GradeRepository;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    GradeRepository gradeRepository;

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

    @BeforeEach
    public void setUp(){
        basicGrade = Grade.builder()
                        .gradeName(GradeName.BASIC)
                .priority(4)
                .pointRate(new BigDecimal("0.01"))
                .minAmount(new BigDecimal(0))
                .maxAmount(new BigDecimal("100000"))
                .build();
        goldGrade = Grade.builder()
                .gradeName(GradeName.GOLD)
                .priority(3)
                .pointRate(new BigDecimal("0.02"))
                .minAmount(new BigDecimal(100000))
                .maxAmount(new BigDecimal("200000"))
                .build();
        royalGrade = Grade.builder()
                .gradeName(GradeName.ROYAL)
                .priority(2)
                .pointRate(new BigDecimal("0.025"))
                .minAmount(new BigDecimal(200000))
                .maxAmount(new BigDecimal("300000"))
                .build();
        platinumGrade = Grade.builder()
                .gradeName(GradeName.PLATINUM)
                .priority(1)
                .pointRate(new BigDecimal("0.03"))
                .minAmount(new BigDecimal(300000))
                .build();

        basicGrade = gradeRepository.save(basicGrade);
        goldGrade = gradeRepository.save(goldGrade);
        royalGrade = gradeRepository.save(royalGrade);
        platinumGrade = gradeRepository.save(platinumGrade);
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

        member.setName("윈터수정");
        member.setPoint(new BigDecimal("5000"));
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
