package com.nhnacademy.illuwa.domain.point.pointpolicy.controller;

import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.point.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.point.pointpolicy.service.PointPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/point-policies")
@RequiredArgsConstructor
@Tag(name = "포인트 정책 API", description = "포인트 정책 생성, 조회, 수정, 삭제 API")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @Operation(summary = "포인트 정책 생성", description = "새로운 포인트 정책을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "포인트 정책 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @PostMapping
    public ResponseEntity<PointPolicyResponse> createPointPolicy(@Valid @RequestBody PointPolicyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointPolicyService.createPointPolicy(request));
    }

    @Operation(summary = "포인트 정책 목록 조회", description = "등록된 모든 포인트 정책을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 정책 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<PointPolicyResponse>> getPointPolicyList() {
        return ResponseEntity.status(HttpStatus.OK).body(pointPolicyService.findAllPointPolicy());
    }

    @Operation(summary = "단일 포인트 정책 조회", description = "특정 policyKey에 해당하는 포인트 정책을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 정책 조회 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 포인트 정책")
    @GetMapping("/{policyKey}")
    public ResponseEntity<PointPolicyResponse> getPointPolicy(@PathVariable String policyKey) {
        return ResponseEntity.status(HttpStatus.OK).body(pointPolicyService.findByPolicyKey(policyKey));
    }

    @Operation(summary = "포인트 정책 수정", description = "특정 policyKey에 해당하는 포인트 정책을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 정책 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 포인트 정책")
    @PutMapping("/{policyKey}")
    public ResponseEntity<PointPolicyResponse> updatePointPolicy(@PathVariable String policyKey,
                                                                 @Valid @RequestBody PointPolicyUpdateRequest request) {
        PointPolicyResponse response = pointPolicyService.updatePointPolicy(policyKey, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "포인트 정책 삭제", description = "특정 policyKey에 해당하는 포인트 정책을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "포인트 정책 삭제 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 포인트 정책")
    @DeleteMapping("/{policyKey}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable String policyKey) {
        pointPolicyService.deletePointPolicy(policyKey);
        return ResponseEntity.ok().build();
    }
}
