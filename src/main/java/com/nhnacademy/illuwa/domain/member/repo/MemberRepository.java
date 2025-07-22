package com.nhnacademy.illuwa.domain.member.repo;

import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository{
    Optional<Member> findByEmail(String email);

    Optional<Member> findByContact(String contact);

    List<Member> findByRole(Role role);

    List<Member> findMembersByStatus(Status status);

    Optional<Member> findByPaycoId(String paycoId);

    @Query("SELECT m FROM Member m WHERE MONTH(m.birth) = :month")
    List<Member> findMemberByBirthMonth(@Param("month")int month);
}
