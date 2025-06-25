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

@EqualsAndHashCode
@Getter
@Table(name = "members")
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long memberId;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @Column(name = "birth", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @Setter
    @Column(name = "email", nullable = false)
    private String email;

    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Setter
    @Column(name = "contact", nullable = false)
    private String contact;

    @Setter
    @ManyToOne
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Setter
    @Column(name = "point", nullable = false)
    private BigDecimal point = BigDecimal.ZERO;

    @Setter
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Setter
    @Column(name = "last_login_at", nullable = false)
    private LocalDateTime lastLoginAt = LocalDateTime.now();

    @Builder
    public Member(String name, LocalDate birth, String email, String password,
                  Role role, String contact, Grade grade, BigDecimal point,
                  Status status, LocalDateTime lastLoginAt) {
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.role = (role != null) ? role : Role.USER;
        this.contact = contact;
        this.grade = grade;
        this.point = (point != null) ? point : BigDecimal.ZERO;
        this.status = (status != null) ? status : Status.ACTIVE;
        this.lastLoginAt = (lastLoginAt != null) ? lastLoginAt : LocalDateTime.now();
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

}
