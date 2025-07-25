package com.nhnacademy.illuwa.domain.point.utils;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.InactiveMemberException;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.point.exception.InvalidPointOperationException;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.point.pointpolicy.entity.enums.PolicyStatus;
import com.nhnacademy.illuwa.domain.point.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.point.pointpolicy.service.PointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class PointManager {

    private final MemberRepository memberRepository;
    private final PointPolicyService pointPolicyService;
    private final PointHistoryService pointHistoryService;

    public BigDecimal getMemberPoint(long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return memberRepository.findPoint(memberId);
    }

    //단순 포인트 업데이트 수행
    private BigDecimal updateMemberPoint(long memberId, BigDecimal point) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        member.changePoint(member.getPoint().add(point));
        return member.getPoint();
    }

    //사용한 포인트 처리
    public PointHistoryResponse processUsedPoint(UsedPointRequest request) {
        BigDecimal orgPoint = memberRepository.findPoint(request.getMemberId());
        if (orgPoint.compareTo(request.getUsedPoint()) < 0) {
            throw new InvalidPointOperationException("소유한 포인트보다 더 많은 포인트를 차감하는 것은 불가합니다.");
        }
        BigDecimal point = request.getUsedPoint().negate();  //음수 전환
        BigDecimal balance = updateMemberPoint(request.getMemberId(), point);
        PointHistoryRequest historyRequest = PointHistoryRequest.builder()
                .memberId(request.getMemberId())
                .type(PointHistoryType.DEDUCT)
                .reason(PointReason.USED_IN_ORDER)
                .amount(request.getUsedPoint())
                .balance(balance)
                .build();
        return pointHistoryService.recordPointHistory(historyRequest);
    }


    //주문적립 포인트 처리
    public Optional<PointHistoryResponse> processOrderPoint(PointAfterOrderRequest request) {
        if (memberRepository.isNotActiveMember(request.getMemberId())) {
            throw new InactiveMemberException(request.getMemberId());
        }
        BigDecimal point = calculateByOrder(request);
        PointHistoryRequest historyRequest = PointHistoryRequest.builder()
                .memberId(request.getMemberId())
                .type(PointHistoryType.EARN)
                .reason(PointReason.PURCHASE)
                .amount(point)
                .balance(updateMemberPoint(request.getMemberId(), point))
                .build();
        return Optional.of(pointHistoryService.recordPointHistory(historyRequest));
    }

    //환불 포인트 적립
    public PointHistoryResponse processRefundPoint(PointAfterOrderRequest request) {
        BigDecimal balance = updateMemberPoint(request.getMemberId(), request.getPrice());
        PointHistoryRequest historyRequest = PointHistoryRequest.builder()
                .memberId(request.getMemberId())
                .type(PointHistoryType.EARN)
                .reason(PointReason.REFUND)
                .amount(request.getPrice())
                .balance(balance)
                .build();
        return pointHistoryService.recordPointHistory(historyRequest);
    }

    //이벤트 포인트 처리
    public Optional<PointHistoryResponse> processEventPoint(long memberId, PointReason reason, BigDecimal point) {
        if (memberRepository.isNotActiveMember(memberId)) {
            return Optional.empty();
        }

        PointPolicyResponse policy;
        if(!reason.getPolicyKey().orElse("").equals("grade_event")){
             policy = pointPolicyService.findByPolicyKey(
                    reason.getPolicyKey().orElseThrow(PointPolicyNotFoundException::new));
            if (!PolicyStatus.ACTIVE.equals(policy.getStatus())) {
                return Optional.empty();
            }
            if (point == null) {
                point = policy.getValue();
            }
        }

        PointHistoryRequest historyRequest = PointHistoryRequest.builder()
                .memberId(memberId)
                .type(PointHistoryType.EARN)
                .reason(reason)
                .amount(point)
                .balance(updateMemberPoint(memberId, point))
                .build();

        return Optional.of(pointHistoryService.recordPointHistory(historyRequest));
    }

    //주문 이후 포인트 적립
    private BigDecimal calculateByOrder(PointAfterOrderRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(request.getMemberId()));

        Grade memberGrade = member.getGrade();
        BigDecimal netOrderAmount = request.getPrice();

        //기본적립
        BigDecimal defaultPoint = BigDecimal.ZERO;
        PointPolicyResponse defaultPolicy = pointPolicyService.findByPolicyKey(PointReason.PURCHASE.getPolicyKey().orElse(null));
        if (defaultPolicy.getStatus().equals(PolicyStatus.ACTIVE)) {
            defaultPoint = netOrderAmount.multiply(defaultPolicy.getValue());
        }
        //구매 후 등급별 적립
        BigDecimal gradePoint = netOrderAmount.multiply(memberGrade.getPointRate());
        return defaultPoint.add(gradePoint);
    }
}
