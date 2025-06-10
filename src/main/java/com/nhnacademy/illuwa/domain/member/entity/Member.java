package com.nhnacademy.illuwa.domain.member.entity;

import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    private String name;

    private LocalDate birth;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.일반;

    private BigDecimal point = new BigDecimal("0");

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private LocalDateTime lastLoginAt = LocalDateTime.now();

    public Member(String name, LocalDate birth, String email, String password, Role role, String phoneNumber, Grade grade, BigDecimal point, Status status) {
        this.name = name;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.grade = grade;
        this.point = point;
        this.status = status;
    }
}
