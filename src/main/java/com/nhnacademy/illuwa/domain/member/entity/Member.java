package com.nhnacademy.illuwa.domain.member.entity;

import com.nhnacademy.illuwa.domain.member.entity.enums.Grade;
import com.nhnacademy.illuwa.domain.member.entity.enums.Role;
import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Column(name = "net_order_amount")
    private BigDecimal netOrderAmount = new BigDecimal("0");

    private BigDecimal point = new BigDecimal("0");

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private LocalDateTime lastLoginAt = LocalDateTime.now();

    public void setGrade(Grade newGrade) {
        if (!Objects.equals(this.grade, newGrade)) {
            //등급변경 발생
            this.grade = newGrade;
        }
    }
}
