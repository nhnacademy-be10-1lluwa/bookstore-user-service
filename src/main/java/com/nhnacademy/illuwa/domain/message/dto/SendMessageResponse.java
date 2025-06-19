package com.nhnacademy.illuwa.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageResponse {
    long memberId;
    String email;
    String message;
}
