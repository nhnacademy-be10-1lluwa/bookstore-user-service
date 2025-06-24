package com.nhnacademy.illuwa.domain.pointhistory.service.impl;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.GradeService;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.pointhistory.dto.OrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.pointhistory.util.PointHistoryMapper;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PointValueType;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryMapper pointHistoryMapper;
    private final PointPolicyService pointPolicyService;
    private final GradeService gradeService;
    private final MemberService memberService;

    private static final String BOOK_DEFAULT_RATE = "book_default_rate";

    //pointPolicy 참고해야하는 포인트적립
    //1. joinPoint
    //2. reviewPoint
    //3. photoReviewPoint

    //calculateByOrder
    // 기본 적립 book_default_rate
    //구매 후 포인트 적립 (by Grade)
    //구매 시 사용한 포인트 차감

    //1. 총괄 메서드
    //memberId + reason + 주문정보로 포인트 처리 후 히스토리 남기는 메서드
    @Override
    public PointHistoryResponse processPointHistory(long memberId, PointReason reason, OrderRequest request) {
        int point = calculatePoint(memberId, reason, request);
        memberService.updateMemberPoint(memberId, point);
        return recordPointHistory(memberId, point, reason);
    }

    //1-2. 포인트 계산
    @Override
    public int calculatePoint(long memberId, PointReason reason, OrderRequest request) {
        return reason.getPolicyKey()
                .map(key -> calculatedFromPolicy(pointPolicyService.findByPolicyKey(key), request))
                .orElseGet(() -> calculateByOrder(memberId, reason, request));
    }

    //2-1. 포인트 정책 기반 포인트 계산
    private int calculatedFromPolicy(PointPolicyResponse policy, OrderRequest request) {
        if(policy.getValueType().equals(PointValueType.AMOUNT)){
            return policy.getValue().intValue();
        } else{
            return request.getNetOrderAmount().multiply(policy.getValue()).intValue();
        }
    }

    //2-2 주문으로 인한 포인트 계산
    private int calculateByOrder(long memberId, PointReason reason, OrderRequest request) {
        switch (reason) {
            case PURCHASE :
                GradeName gradeName = GradeName.valueOf(memberService.getMemberById(memberId).getGradeName());
                Grade grade = gradeService.getByGradeName(gradeName);

                BigDecimal netOrderAmount = request.getNetOrderAmount();
                //기본적립
                BigDecimal defaultRate = pointPolicyService.findByPolicyKey(BOOK_DEFAULT_RATE).getValue();
                BigDecimal defaultPoint = netOrderAmount.multiply(defaultRate);

                //구매 후 등급별 적립
                BigDecimal gradePoint = netOrderAmount.multiply(grade.getPointRate());
                return defaultPoint.add(gradePoint).intValue();

            //구매 시 사용한 포인트 차감
            case USED_IN_ORDER:
                return -request.getUsedPoint();
            default :
                throw new UnsupportedOperationException("정책 없이 처리할 수 없는 reason: " + reason);
        }
    }

    //3. 포인트 내역 기록
    @Override
    public PointHistoryResponse recordPointHistory(long memberId, int point, PointReason reason){
        PointHistory pointHistory = PointHistory.builder()
                .memberId(memberId)
                .amount(point)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        PointHistoryType type = point > 0 ? PointHistoryType.EARN : PointHistoryType.USE;
        pointHistory.setType(type);

        return pointHistoryMapper.toDto(pointHistoryRepository.save(pointHistory));
    }
}
