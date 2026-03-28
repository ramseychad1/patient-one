package com.hubaccess.scheduler;

import com.hubaccess.domain.outreach.OutreachRepository;
import com.hubaccess.domain.outreach.PatientOutreach;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutreachRetryJob {

    private final OutreachRepository outreachRepository;

    @Scheduled(fixedDelay = 14_400_000)
    public void checkOutreachRetry() {
        List<PatientOutreach> unresponded = outreachRepository.findUnrespondedConsentAndMi();
        log.info("Outreach retry check: {} unresponded outreach records", unresponded.size());
        // TODO: Create retry outreach for records past threshold
    }
}
