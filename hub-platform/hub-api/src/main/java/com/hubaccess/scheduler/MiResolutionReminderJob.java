package com.hubaccess.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MiResolutionReminderJob {

    @Scheduled(cron = "0 0 8 * * *")
    public void checkMiResolutionReminder() {
        log.info("Running MI resolution reminder check");
        // TODO: Find cases with mi_status='Pending' older than threshold and create reminder tasks
    }
}
