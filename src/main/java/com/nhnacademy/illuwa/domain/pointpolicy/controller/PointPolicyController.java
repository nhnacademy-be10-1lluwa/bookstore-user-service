package com.nhnacademy.illuwa.domain.pointpolicy.controller;

import com.nhnacademy.illuwa.domain.pointpolicy.dto.PointPolicyCreateRequest;
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
@RequestMapping("/admin/point-policies")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @PostMapping
    public ResponseEntity<PointPolicyResponse> createPointPolicy(@Valid @RequestBody PointPolicyCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointPolicyService.createPointPolicy(request));
    }

    @GetMapping
    public ResponseEntity<List<PointPolicyResponse>> getPointPolicyList(){
        return ResponseEntity.status(HttpStatus.OK).body(pointPolicyService.findAllPointPolicy());
    }

    @GetMapping("/{policyKey}")
    public ResponseEntity<PointPolicyResponse> getPointPolicy(@PathVariable String policyKey){
        return ResponseEntity.status(HttpStatus.OK).body(pointPolicyService.findByPolicyKey(policyKey));
    }

    @PutMapping("/{policyKey}")
    public ResponseEntity<PointPolicyResponse> updatePointPolicy(@PathVariable String policyKey, @Valid @RequestBody PointPolicyUpdateRequest request){
        PointPolicyResponse response = pointPolicyService.updatePointPolicy(policyKey, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{policyKey}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable String policyKey){
        pointPolicyService.deletePointPolicy(policyKey);
        return ResponseEntity.ok().build();
    }
}
