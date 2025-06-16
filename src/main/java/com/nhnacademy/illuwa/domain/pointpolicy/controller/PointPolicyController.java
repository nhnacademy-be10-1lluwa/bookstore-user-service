package com.nhnacademy.illuwa.domain.pointpolicy.controller;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyResponse;
import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.pointpolicy.service.PointPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members/admin/point-policies")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @GetMapping
    public ResponseEntity<List<PointPolicyResponse>> getAllPointPolicies(){
        return ResponseEntity.status(HttpStatus.OK).body(pointPolicyService.getAllPointPolicy());
    }

    @PatchMapping("/{policyKey}")
    public ResponseEntity<PointPolicyResponse> updatePointPolicy(@PathVariable String policyKey, @Valid @RequestBody PointPolicyUpdateRequest request){
        PointPolicyResponse response = pointPolicyService.updatePointPolicy(policyKey, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
