package com.nhnacademy.illuwa.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    String text;
    Map.Entry<String, Object> attachContent;
}
