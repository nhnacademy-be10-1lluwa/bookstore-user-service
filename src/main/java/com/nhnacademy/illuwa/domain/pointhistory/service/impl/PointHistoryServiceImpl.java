package com.nhnacademy.illuwa.domain.pointhistory.service.impl;

import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.pointhistory.util.PointHistoryMapper;
import com.sun.jdi.request.InvalidRequestStateException;
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
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryMapper pointHistoryMapper;

    @Override
    public PointHistoryResponse recordPointHistory(long memberId, BigDecimal point, PointReason reason){
        BigDecimal orgPoint = memberRepository.findPoint(memberId);

        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(point)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        PointHistoryType type = point.compareTo(java.math.BigDecimal.ZERO) > 0 ? PointHistoryType.EARN : PointHistoryType.USE;
        pointHistory.setType(type);
        if(type.equals(PointHistoryType.USE) && orgPoint.compareTo(point) < 0){
            throw new InvalidRequestStateException("현재 포인트보다 더 많은 포인트를 사용하는 것은 불가합니다.");
        }
        pointHistory.setBalance(type.equals(PointHistoryType.EARN) ? orgPoint.add(point) : orgPoint.subtract(point));

        return pointHistoryMapper.toDto(pointHistoryRepository.save(pointHistory));
    }

    @Override
    public List<PointHistoryResponse> getMemberPointHistories(long memberId){
        return pointHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(pointHistoryMapper::toDto).toList();
    }
}
