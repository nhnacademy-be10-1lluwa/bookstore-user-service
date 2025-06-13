package com.nhnacademy.illuwa.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InactiveRestoreResponse {
    boolean success;
    long memberId;
    String message;
}
