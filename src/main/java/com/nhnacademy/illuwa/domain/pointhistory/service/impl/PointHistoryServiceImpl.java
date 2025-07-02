package com.nhnacademy.illuwa.domain.pointhistory.service.impl;

import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.pointhistory.util.PointHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryMapper pointHistoryMapper;

    @Override
    public PointHistoryResponse recordPointHistory(long memberId, BigDecimal point, PointReason reason){
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(point)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        PointHistoryType type = point.compareTo(java.math.BigDecimal.ZERO) > 0 ? PointHistoryType.EARN : PointHistoryType.USE;
        pointHistory.setType(type);

        return pointHistoryMapper.toDto(pointHistoryRepository.save(pointHistory));
    }

    @Override
    public List<PointHistoryResponse> getMemberPointHistories(long memberId){
        return pointHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(pointHistoryMapper::toDto).toList();
    }
}
