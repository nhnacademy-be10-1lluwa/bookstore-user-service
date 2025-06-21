package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberGradeRepository{
    Optional<Member> findByEmail(String email);

    List<Member> findByRole(Role role);

    Optional<Member> getMemberByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
}
