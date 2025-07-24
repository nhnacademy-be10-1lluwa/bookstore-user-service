package com.nhnacademy.illuwa.domain.member.dto;

import com.nhnacademy.illuwa.domain.member.entity.enums.Status;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class InactiveCheckResponse {
    private long memberId;
    private String name;
    private String email;
    private Status status;
}