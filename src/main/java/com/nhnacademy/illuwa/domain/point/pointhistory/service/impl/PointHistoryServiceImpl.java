package com.nhnacademy.illuwa.domain.point.pointhistory.service.impl;

import com.nhnacademy.illuwa.domain.point.pointhistory.utils.PointHistoryMapper;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.point.pointhistory.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryMapper pointHistoryMapper;

    @Override
    public PointHistoryResponse recordPointHistory(PointHistoryRequest request){
        PointHistory pointHistory = PointHistory.builder()
                .memberId(request.getMemberId())
                .amount(request.getAmount())
                .type(request.getType())
                .reason(request.getReason())
                .balance(request.getBalance())
                .createdAt(request.getCreatedAt())
                .build();
        return pointHistoryMapper.toDto(pointHistoryRepository.save(pointHistory));
    }

    @Override
    public List<PointHistoryResponse> getMemberPointHistories(long memberId){
        return pointHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(pointHistoryMapper::toDto).toList();
    }

    @Override
    public Page<PointHistoryResponse> getPagedMemberPointHistories(long memberId, String type, Pageable pageable) {
        Page<PointHistory> page;

        if ("EARN".equalsIgnoreCase(type)) {
            page = pointHistoryRepository.findByMemberIdAndTypeOrderByCreatedAtDesc(memberId, PointHistoryType.EARN, pageable);
        } else if ("USE".equalsIgnoreCase(type)) {
            page = pointHistoryRepository.findByMemberIdAndTypeOrderByCreatedAtDesc(memberId, PointHistoryType.DEDUCT, pageable);
        } else {
            page = pointHistoryRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        }
        return page.map(pointHistoryMapper::toDto);
    }

}
