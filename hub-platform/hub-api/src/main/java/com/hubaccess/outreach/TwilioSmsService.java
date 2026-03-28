package com.hubaccess.outreach;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("production")
public class TwilioSmsService implements SmsService {

    @Override
    public SmsResult send(String toPhone, String messageBody, String uniqueUrl, String accessCode) {
        // TODO: Implement Twilio integration post-MVP
        log.warn("TwilioSmsService called but not implemented. Would send to: {}", toPhone);
        return SmsResult.builder()
            .success(false)
            .errorMessage("Twilio integration not yet implemented")
            .build();
    }
}
