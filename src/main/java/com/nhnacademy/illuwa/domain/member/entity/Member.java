package com.nhnacademy.illuwa.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
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
    @Setter // 임시
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
    private String contact;

    @Setter
    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.일반;

    @Setter
    @Column(name = "net_order_amount")
    private BigDecimal netOrderAmount = new BigDecimal("0");

    @Setter
    private BigDecimal point = new BigDecimal("0");

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Setter
    private LocalDateTime lastLoginAt = LocalDateTime.now();

    @Builder
    public Member(String name, LocalDate birth, String email, String password,
                  Role role, String contact, Grade grade,
                  BigDecimal netOrderAmount, BigDecimal point,
                  Status status, LocalDateTime lastLoginAt) {
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.role = (role != null) ? role : Role.USER;
        this.contact = contact;
        this.grade = (grade != null) ? grade : Grade.일반;
        this.netOrderAmount = (netOrderAmount != null) ? netOrderAmount : BigDecimal.ZERO;
        this.point = (point != null) ? point : BigDecimal.ZERO;
        this.status = (status != null) ? status : Status.ACTIVE;
        this.lastLoginAt = (lastLoginAt != null) ? lastLoginAt : LocalDateTime.now();
    }

}
