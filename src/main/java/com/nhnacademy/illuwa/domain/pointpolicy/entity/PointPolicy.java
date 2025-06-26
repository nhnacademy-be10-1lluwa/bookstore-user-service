package com.nhnacademy.illuwa.domain.pointpolicy.entity;

import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "point_policy")
public class PointPolicy {
    @Id
    @Column(name = "policy_key")
    private String policyKey;
    //join_point 회원가입 포인트
    //review_point 리뷰 포인트
    //photo_review_point 포토리뷰 포인트
    //book_default_rate 도서 기본 적립률

    @Column(name = "value", precision = 10, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "value_type", nullable = false)
    private PointValueType valueType;

    @Column(name = "description", nullable = false)
    private String description;
}

