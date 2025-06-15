package com.nhnacademy.illuwa.domain.point.entity.pointhistory;

import com.nhnacademy.illuwa.domain.point.entity.pointhistory.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "point_history")
public class PointHistory {
    @Id
    @GeneratedValue
    @Column(name = "point_history_id")
    private long pointHistoryId;

    @Column(nullable = false)
    private int amount;

    private String reason;

    @Column(nullable = false)
    private PointHistoryType type;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
