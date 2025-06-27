package com.nhnacademy.illuwa.domain.pointhistory.controller;

import com.nhnacademy.illuwa.domain.pointhistory.dto.PointAfterOrderRequest;
import com.nhnacademy.illuwa.domain.pointhistory.dto.PointHistoryResponse;
import com.nhnacademy.illuwa.domain.pointhistory.dto.UsedPointRequest;
import com.nhnacademy.illuwa.domain.pointhistory.entity.enums.PointReason;
import com.nhnacademy.illuwa.domain.pointhistory.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members/{memberId}")
@RequiredArgsConstructor
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    @GetMapping("/point-histories")
    public ResponseEntity<List<PointHistoryResponse>> getMemberPointHistories(@PathVariable long memberId){
        return ResponseEntity.ok().body(pointHistoryService.getMemberPointHistories(memberId));
    }

    @PostMapping("/points/event")
    public ResponseEntity<PointHistoryResponse> eventPointHistory(@RequestParam("reason") PointReason reason, @PathVariable long memberId){
        return ResponseEntity.status(HttpStatus.CREATED).body(pointHistoryService.processEventPoint(memberId, reason));
    }

    @PostMapping("/points/order/earn")
    public ResponseEntity<PointHistoryResponse> earnOrderPointHistory(@RequestBody PointAfterOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(pointHistoryService.processOrderPoint(request));
    }

    @PostMapping("/points/order/use")
    public ResponseEntity<PointHistoryResponse> usedPointHistory(@RequestBody UsedPointRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(pointHistoryService.processUsedPoint(request));
    }
}
