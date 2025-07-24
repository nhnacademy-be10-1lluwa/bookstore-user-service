package com.nhnacademy.illuwa.domain.point.pointhistory.controller;

import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.point.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.point.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.point.utils.PointManager;
import com.nhnacademy.illuwa.domain.point.pointhistory.service.PointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회원 포인트 API", description = "포인트 조회, 적립, 사용, 반품 처리 등의 API를 제공합니다.")
public class PointHistoryController {

    private final PointManager pointManager;
    private final PointHistoryService pointHistoryService;

    @Operation(summary = "회원 포인트 조회", description = "회원의 현재 보유 포인트를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 조회 성공")
    @GetMapping
    public ResponseEntity<BigDecimal> getMemberPoint(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.ok(pointManager.getMemberPoint(memberId));
    }

    @Operation(summary = "회원 포인트 내역 전체 조회", description = "회원의 포인트 내역 전체를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 내역 조회 성공")
    @GetMapping("/histories")
    public ResponseEntity<List<PointHistoryResponse>> getMemberPointHistories(@RequestHeader("X-USER-ID") long memberId) {
        return ResponseEntity.ok(pointHistoryService.getMemberPointHistories(memberId));
    }

    @Operation(summary = "회원 포인트 내역 페이징 조회", description = "회원의 포인트 내역을 페이징하여 조회합니다. type: 적립, 사용, 환불 또는 ALL")
    @ApiResponse(responseCode = "200", description = "페이징 조회 성공")
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

    // 내부 통신 API
    @Operation(summary = "이벤트 포인트 지급", description = "이벤트 사유에 따라 포인트를 지급합니다.")
    @ApiResponse(responseCode = "201", description = "포인트 지급 성공")
    @ApiResponse(responseCode = "204", description = "포인트 지급 조건 불충족")
    @PostMapping("/event")
    public ResponseEntity<PointHistoryResponse> earnEventPoint(@RequestHeader("X-USER-ID") long memberId,
                                                               @RequestParam("reason") PointReason reason) {
        return pointManager.processEventPoint(memberId, reason, null)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "주문 포인트 적립", description = "주문 완료 후 적립 포인트를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "주문 포인트 적립 성공")
    @PostMapping("/order/earn")
    public ResponseEntity<PointHistoryResponse> earnPointAfterOrder(@RequestBody PointAfterOrderRequest request) {
        return pointManager.processOrderPoint(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "주문 시 포인트 사용", description = "주문 시 사용한 포인트를 차감합니다.")
    @ApiResponse(responseCode = "201", description = "포인트 사용 처리 완료")
    @PostMapping("/order/use")
    public ResponseEntity<PointHistoryResponse> deductPointInOrder(@RequestBody UsedPointRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processUsedPoint(request));
    }

    @Operation(summary = "반품/주문취소 포인트 환불", description = "반품이나 주문취소 시 포인트를 환불합니다.")
    @ApiResponse(responseCode = "201", description = "환불 포인트 적립 성공")
    @PostMapping("/order/return")
    public ResponseEntity<PointHistoryResponse> earnPointAfterRefund(@RequestBody PointAfterOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointManager.processRefundPoint(request));
    }
}