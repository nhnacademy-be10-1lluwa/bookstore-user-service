package com.nhnacademy.illuwa.domain.pointhistory.service.impl;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.GradeService;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.service.MemberService;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.pointhistory.entity.PointHistory;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.repo.PointHistoryRepository;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.pointhistory.util.PointHistoryMapper;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
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
    private final PointPolicyService pointPolicyService;
    private final GradeService gradeService;
    private final MemberService memberService;

    private static final String BOOK_DEFAULT_RATE = "book_default_rate";

    @Override
    public PointHistoryResponse processUsedPoint(UsedPointRequest request){
        BigDecimal point = request.getUsedPoint().negate();  //음수 전환
        memberService.updateMemberPoint(request.getMemberId(), point);
        return recordPointHistory(request.getMemberId(), point, PointReason.USED_IN_ORDER);
    }

    @Override
    public PointHistoryResponse processOrderPoint(PointAfterOrderRequest request) {
        if(memberService.isNotActiveMember(request.getMemberId())){
            throw new MemberNotFoundException();
        }
        BigDecimal point = calculateByOrder(request);
        memberService.updateMemberPoint(request.getMemberId(), point);
        return recordPointHistory(request.getMemberId(), point, PointReason.PURCHASE);
    }

    @Override
    public PointHistoryResponse processEventPoint(long memberId, PointReason reason) {
        if(memberService.isNotActiveMember(memberId)){
            throw new MemberNotFoundException();
        }
        PointPolicyResponse policy = pointPolicyService.findByPolicyKey(reason.getPolicyKey().orElseThrow(PointPolicyNotFoundException::new));
        BigDecimal point = calculatedFromPolicy(policy);
        memberService.updateMemberPoint(memberId, point);
        return recordPointHistory(memberId, point, reason);
    }

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

    private BigDecimal calculatedFromPolicy(PointPolicyResponse policy) {
        //AMOUNT 타입만 있지만 추후 RATE 타입은 다르게 리턴
        return policy.getValue();
    }

    private BigDecimal calculateByOrder(PointAfterOrderRequest request) {
        GradeName gradeName = GradeName.valueOf(memberService.getMemberById(request.getMemberId()).getGradeName());
        Grade grade = gradeService.getByGradeName(gradeName);

        BigDecimal netOrderAmount = request.getNetOrderAmount();

        //기본적립
        BigDecimal defaultRate = pointPolicyService.findByPolicyKey(BOOK_DEFAULT_RATE).getValue();
        BigDecimal defaultPoint = netOrderAmount.multiply(defaultRate);
        //구매 후 등급별 적립
        BigDecimal gradePoint = netOrderAmount.multiply(grade.getPointRate());
        return defaultPoint.add(gradePoint);
    }

}
