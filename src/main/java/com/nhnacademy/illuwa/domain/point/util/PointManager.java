package com.nhnacademy.illuwa.domain.point.util;

import com.nhnacademy.illuwa.domain.grade.entity.Grade;
import com.nhnacademy.illuwa.domain.grade.entity.enums.GradeName;
import com.nhnacademy.illuwa.domain.grade.service.GradeService;
import com.nhnacademy.illuwa.domain.member.entity.Member;
import com.nhnacademy.illuwa.domain.member.exception.MemberNotFoundException;
import com.nhnacademy.illuwa.domain.member.repo.MemberRepository;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.entity.enums.PolicyStatus;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.InactivePointPolicyException;
import com.nhnacademy.illuwa.domain.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Transactional
public class PointManager {

    private final MemberRepository memberRepository;
    private final GradeService gradeService;
    private final PointPolicyService pointPolicyService;
    private final PointHistoryService pointHistoryService;

    private static final String BOOK_DEFAULT_RATE = "book_default_rate";

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
        BigDecimal point = request.getUsedPoint().negate();  //음수 전환
        BigDecimal balance = updateMemberPoint(request.getMemberId(), point);
        return pointHistoryService.recordPointHistory(request.getMemberId(), point, PointReason.USED_IN_ORDER);
    }

    //주문적립 포인트 처리
    public PointHistoryResponse processOrderPoint(PointAfterOrderRequest request) {
        if(memberRepository.isNotActiveMember(request.getMemberId())){
            throw new MemberNotFoundException();
        }
        BigDecimal point = calculateByOrder(request);
        updateMemberPoint(request.getMemberId(), point);
        return pointHistoryService.recordPointHistory(request.getMemberId(), point, PointReason.PURCHASE);
    }

    //이벤트 포인트 처리
    public PointHistoryResponse processEventPoint(long memberId, PointReason reason) {
        if(memberRepository.isNotActiveMember(memberId)){
            throw new MemberNotFoundException();
        }
        PointPolicyResponse policy = pointPolicyService.findByPolicyKey(reason.getPolicyKey().orElseThrow(PointPolicyNotFoundException::new));
        if(policy.getStatus().equals(PolicyStatus.INACTIVE)){
            throw new InactivePointPolicyException(policy.getPolicyKey());
        }
        BigDecimal point = calculatedFromPolicy(policy);
        updateMemberPoint(memberId, point);
        return pointHistoryService.recordPointHistory(memberId, point, reason);
    }


    //정책 기반 포인트 계산 - 이벤트 종류는 보통 AMOUNT만 사용
    private BigDecimal calculatedFromPolicy(PointPolicyResponse policy) {
        //AMOUNT 타입만 있지만 추후 RATE 타입은 다르게 리턴 필요
        return policy.getValue();
    }

    //주문 이후 포인트 적립
    private BigDecimal calculateByOrder(PointAfterOrderRequest request) {
        String memberGrade = memberRepository.findById(request.getMemberId()).get().getGrade().getGradeName().toString();
        GradeName gradeName = GradeName.valueOf(memberGrade);
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
