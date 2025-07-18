package com.nhnacademy.illuwa.domain.point.pointhistory.controller;

import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.utils.PointManager;
import com.nhnacademy.illuwa.domain.point.pointhistory.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<BigDecimal> getMemberPoint(@RequestHeader("X-USER-ID") long memberId) {
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
     * 포인트 내역 페이지네이션 조회
     */
    @GetMapping("/histories/paged")
    public ResponseEntity<Page<PointHistoryResponse>> getPagedMemberPointHistories(
            @RequestHeader("X-USER-ID") long memberId,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PointHistoryResponse> result = pointHistoryService.getPagedMemberPointHistories(memberId, type, pageable);
        return ResponseEntity.ok(result);
    }


    /* 내부 통신 api*/
    /**
     * 이벤트 포인트 지급
     */
    @PostMapping("/event")
    public ResponseEntity<PointHistoryResponse> earnEventPoint(@RequestHeader("X-USER-ID") long memberId,
                                               @RequestParam("reason") PointReason reason) {
        return pointManager.processEventPoint(memberId, reason, null)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.noContent().build());
    }
    /**
     * 주문에 의한 포인트 적립
     */
    @PostMapping("/order/earn")
    public ResponseEntity<PointHistoryResponse> earnPointAfterOrder(@RequestBody PointAfterOrderRequest request) {
        return pointManager.processOrderPoint(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.noContent().build());
    }
    /**
     * 주문에 의한 포인트 사용
     */
    @PostMapping("/order/use")
    public ResponseEntity<PointHistoryResponse> deductPointInOrder(@RequestBody UsedPointRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processUsedPoint(request));
    }

    /**
     * 주문취소, 반품에 의한 포인트 적립
     */
    @PostMapping("/order/return")
    public ResponseEntity<PointHistoryResponse> earnPointAfterRefund(@RequestBody PointAfterOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processRefundPoint(request));
    }
}