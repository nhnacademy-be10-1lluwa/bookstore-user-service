package com.nhnacademy.illuwa.domain.pointhistory.entity;

import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "point_history")
public class PointHistory {
    @Id
    @GeneratedValue
    @Column(name = "point_history_id")
    private long pointHistoryId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private PointReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointHistoryType type;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public PointHistory(int amount, PointReason reason, PointHistoryType type, LocalDateTime createdAt, Member member) {
        this.amount = amount;
        this.reason = reason;
        this.type = type;
        this.createdAt = createdAt;
        this.member = member;
    }
}
