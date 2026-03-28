package com.hubaccess.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsentExpiryJob {

    @Scheduled(cron = "0 0 6 * * *")
    public void checkConsentExpiry() {
        log.info("Running consent expiry check");
        // TODO: Query outreach records with expired URLs and update consent_status on hub_case
    }
}
