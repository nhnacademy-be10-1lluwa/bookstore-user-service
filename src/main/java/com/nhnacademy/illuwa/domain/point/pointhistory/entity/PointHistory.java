package com.nhnacademy.illuwa.domain.point.pointhistory.entity;

import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "point_history")
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private long pointHistoryId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private PointReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointHistoryType type;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "member_id")
    private long memberId;

    @Builder
    public PointHistory(BigDecimal amount, PointReason reason, PointHistoryType type, BigDecimal balance, LocalDateTime createdAt, long memberId) {
        this.amount = amount;
        this.reason = reason;
        this.type = type;
        this.balance = balance;
        this.createdAt = createdAt;
        this.memberId = memberId;
    }
}
