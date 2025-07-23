package com.nhnacademy.illuwa.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Table(name = "members")
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long memberId;

    @Column(name = "payco_id")
    private String paycoId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "contact", unique = true, nullable = false)
    private String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Column(name = "point", nullable = false)
    private BigDecimal point = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String paycoId, String name, LocalDate birth, String email, String password,
                  Role role, String contact, Grade grade, BigDecimal point,
                  Status status, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.paycoId = paycoId;
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.grade = grade;
        this.role = (role != null) ? role : Role.USER;
        this.point = (point != null) ? point : BigDecimal.ZERO;
        this.status = (status != null) ? status : Status.ACTIVE;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }

    // LocalDate 값 편하게 넣기 위한 커스텀 Builder
    public static class MemberBuilder {
        public MemberBuilder birth(String birth) {
            this.birth = LocalDate.parse(birth);
            return this;
        }

        public MemberBuilder birth(LocalDate birth) {
            this.birth = birth;
            return this;
        }
    }

    public void changeName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void changeBirth(LocalDate birth) {
        if (birth != null) {
            this.birth = birth;
        }
    }

    public void changeEmail(String email) {
        if (email != null) {
            this.email = email;
        }
    }

    public void changePassword(String encodedPassword) {
        if (password != null) {
            this.password = encodedPassword;
        }
    }

    public void changeRole(Role role) {
        if (role != null) {
            this.role = role;
        }
    }

    public void changeContact(String contact) {
        if (contact != null) {
            this.contact = contact;
        }
    }

    public void changeGrade(Grade grade) {
        if (grade != null) {
            this.grade = grade;
        }
    }

    public void changePoint(BigDecimal point) {
        if (point == null || point.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
        this.point = point;
    }

    public void changeStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }

    public void changeLastLoginAt(LocalDateTime lastLoginAt) {
        if (lastLoginAt != null) {
            this.lastLoginAt = lastLoginAt;
        }
    }

}