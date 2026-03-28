package com.hubaccess.outreach;

public interface SmsService {
    SmsResult send(String toPhone, String messageBody, String uniqueUrl, String accessCode);
}
