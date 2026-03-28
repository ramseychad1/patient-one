package com.hubaccess.outreach;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SmsResult {
    private boolean success;
    private String messageId;
    private String errorMessage;
}
