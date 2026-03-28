package com.hubaccess.outreach;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Profile("!production")
public class MockSmsService implements SmsService {

    @Override
    public SmsResult send(String toPhone, String messageBody, String uniqueUrl, String accessCode) {
        log.info("=== MOCK SMS ===");
        log.info("To: {}", toPhone);
        log.info("Body: {}", messageBody);
        log.info("URL: {}", uniqueUrl);
        log.info("Access Code: {}", accessCode);
        log.info("================");

        return SmsResult.builder()
            .success(true)
            .messageId("mock-" + UUID.randomUUID())
            .build();
    }
}
