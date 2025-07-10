package com.nhnacademy.illuwa.domain.pointhistory.controller;

import com.nhnacademy.illuwa.domain.member.dto.MemberPointResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.util.PointManager;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members/points")
@RequiredArgsConstructor
public class PointHistoryController {

    private final PointManager pointManager;
    private final PointHistoryService pointHistoryService;
    /**
     * 포인트 조회
     */
    @GetMapping
    public ResponseEntity<MemberPointResponse> getMemberPoint(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.ok(pointManager.getMemberPoint(memberId));
    }
    /**
     * 포인트 내역 조회
     */
    @GetMapping("/histories")
    public ResponseEntity<List<PointHistoryResponse>> getMemberPointHistories(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.ok(pointHistoryService.getMemberPointHistories(memberId));
    }
    /**
     * 이벤트 포인트 지급
     */
    @PostMapping("/event")
    public ResponseEntity<PointHistoryResponse> earnEventPoint(@RequestHeader("X-USER-ID") long memberId,
                                               @RequestParam("reason") PointReason reason) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processEventPoint(memberId, reason));
    }
    /**
     * 주문에 의한 포인트 적립
     */
    @PostMapping("/order/earn")
    public ResponseEntity<PointHistoryResponse> earnPointAfterOrder(@RequestBody PointAfterOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processOrderPoint(request));
    }
    /**
     * 주문에 의한 포인트 사용
     */
    @PostMapping("/order/use")
    public ResponseEntity<PointHistoryResponse> usePointForOrder(@RequestBody UsedPointRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processUsedPoint(request));
    }
}
