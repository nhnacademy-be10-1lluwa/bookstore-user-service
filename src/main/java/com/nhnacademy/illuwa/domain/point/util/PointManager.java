package com.nhnacademy.illuwa.domain.point.util;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.point.exception.InvalidPointOperationException;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointHistoryType;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PolicyStatus;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
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

    public BigDecimal getMemberPoint(long memberId){
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
    public PointHistoryResponse processUsedPoint(UsedPointRequest request){
        BigDecimal orgPoint = memberRepository.findPoint(request.getMemberId());
        if(orgPoint.compareTo(request.getUsedPoint()) < 0){
            throw new InvalidPointOperationException("소유한 포인트보다 더 많은 포인트를 사용하는 것은 불가합니다.");
        }
        BigDecimal point = request.getUsedPoint().negate();  //음수 전환
        BigDecimal balance = updateMemberPoint(request.getMemberId(), point);
        PointHistoryRequest historyRequest = PointHistoryRequest.builder()
                .memberId(request.getMemberId())
                .type(PointHistoryType.USE)
                .reason(PointReason.USED_IN_ORDER)
                .amount(request.getUsedPoint())
                .balance(balance)
                .build();
        return pointHistoryService.recordPointHistory(historyRequest);
    }

    //주문적립 포인트 처리
    public  Optional<PointHistoryResponse> processOrderPoint(PointAfterOrderRequest request) {
        if(memberRepository.isNotActiveMember(request.getMemberId())){
            throw new MemberNotFoundException();
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

    //이벤트 포인트 처리
    public Optional<PointHistoryResponse> processEventPoint(long memberId, PointReason reason) {
        if(memberRepository.isNotActiveMember(memberId)){
            throw new MemberNotFoundException();
        }
        PointPolicyResponse policy = pointPolicyService.findByPolicyKey(reason.getPolicyKey().orElseThrow(PointPolicyNotFoundException::new));
        PointHistoryRequest historyRequest;

        if(policy.getStatus().equals(PolicyStatus.ACTIVE)){
            BigDecimal point = policy.getValue();
            historyRequest = PointHistoryRequest.builder()
                    .memberId(memberId)
                    .type(PointHistoryType.EARN)
                    .reason(reason)
                    .amount(point)
                    .balance(updateMemberPoint(memberId, point))
                    .build();

            return Optional.of(pointHistoryService.recordPointHistory(historyRequest));
        }
        return Optional.empty();
    }


    //정책 기반 포인트 계산 - 이벤트 종류는 보통 AMOUNT만 사용
/*    private BigDecimal calculatedFromPolicy(PointPolicyResponse policy) {
        //AMOUNT 타입만 있지만 추후 RATE 타입은 다르게 리턴 필요
        if(policy.getValueType().equals(PointValueType.AMOUNT)){
            return policy.getValue();
        }
    }*/

    //주문 이후 포인트 적립
    private BigDecimal calculateByOrder(PointAfterOrderRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        Grade memberGrade = member.getGrade();
        BigDecimal netOrderAmount = request.getNetOrderAmount();

        //기본적립
        BigDecimal defaultPoint = BigDecimal.ZERO;
        PointPolicyResponse defaultPolicy = pointPolicyService.findByPolicyKey(PointReason.PURCHASE.getPolicyKey().get());
        if(defaultPolicy.getStatus().equals(PolicyStatus.ACTIVE)){
            defaultPoint = netOrderAmount.multiply(defaultPolicy.getValue());
        }
        //구매 후 등급별 적립
        BigDecimal gradePoint = netOrderAmount.multiply(memberGrade.getPointRate());
        return defaultPoint.add(gradePoint);
    }
}
