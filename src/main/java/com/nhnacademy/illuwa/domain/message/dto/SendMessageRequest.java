package com.nhnacademy.illuwa.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {
    @Builder.Default
    String botName = "1lluwa";

    String text;

    String recipientName;
    String recipientEmail;

    String attachmentTitle;
    String attachmentText;

    @Builder.Default
    String attachmentColor = "blue";

    public boolean hasAttachment() {
        return attachmentTitle != null || attachmentText != null;
    }

}
