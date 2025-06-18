package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
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

    @Test
    @DisplayName("회원 저장 성공 테스트")
    @Rollback
    void testSave() {
        Member member = createMember("카리나", "karina@naver.com", Grade.로얄, Role.USER);
        Member saved = memberRepository.save(member);

        assertNotNull(saved.getMemberId());
        assertEquals("카리나", saved.getName());
    }


    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    @Rollback
    void testUpdate() {
        Member member = memberRepository.save(createMember("윈터", "winter@naver.com", Grade.일반, Role.USER));

        member.setName("윈터수정");
        member.setPoint(new BigDecimal("5000"));
        Member updated = memberRepository.save(member);

        assertEquals("윈터수정", updated.getName());
        assertEquals(new BigDecimal("5000"), updated.getPoint());
    }

    @Test
    @DisplayName("회원 삭제 성공 테스트")
    @Rollback
    void testDelete() {
        Member member = memberRepository.save(createMember("닝닝", "ningning@naver.com", Grade.로얄, Role.USER));
        Long id = member.getMemberId();

        memberRepository.deleteById(id);
        Optional<Member> found = memberRepository.findById(id);

        assertFalse(found.isPresent());
    }

    @Test
    @Disabled
    @DisplayName("특정 등급 회원 조회 테스트")
    void testFindByGrade() {
        Member m1 = memberRepository.save(createMember("아이유", "iu@naver.com", Grade.로얄, Role.USER));
        Member m2 = memberRepository.save(createMember("태연", "taeyeon@naver.com", Grade.플래티넘, Role.USER));
        Member m3 = memberRepository.save(createMember("슬기", "seulgi@naver.com", Grade.로얄, Role.USER));

        List<Member> royalMembers = memberRepository.findByGrade(Grade.로얄);

        assertEquals(2, royalMembers.size());
        assertTrue(royalMembers.stream().allMatch(m -> m.getGrade() == Grade.로얄));
    }

    @Test
    @DisplayName("특정 역할 회원 조회 테스트")
    void testFindByRole() {
        Member m1 = memberRepository.save(createMember("보아", "boa@naver.com", Grade.일반, Role.ADMIN));
        Member m2 = memberRepository.save(createMember("제니", "jennie@naver.com", Grade.일반, Role.ADMIN));
        Member m3 = memberRepository.save(createMember("지수", "jisoo@naver.com", Grade.일반, Role.USER));

        List<Member> admins = memberRepository.findByRole(Role.ADMIN);

        assertEquals(2, admins.size());
        assertTrue(admins.stream().allMatch(m -> m.getRole() == Role.ADMIN));
    }
}
