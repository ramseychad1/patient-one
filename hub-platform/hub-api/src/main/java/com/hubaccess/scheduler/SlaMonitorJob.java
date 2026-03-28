package com.hubaccess.scheduler;

import com.hubaccess.domain.financial.PriorAuthorization;
import com.hubaccess.domain.financial.PriorAuthorizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlaMonitorJob {

    private final PriorAuthorizationRepository paRepository;

    @Scheduled(fixedDelay = 900_000)
    @Transactional
    public void checkPaSlaBreaches() {
        List<PriorAuthorization> breached = paRepository.findPaSlaBreaches(LocalDate.now());
        for (PriorAuthorization pa : breached) {
            pa.setSlaBreached(true);
            paRepository.save(pa);
            log.warn("PA SLA breached: PA {} for case {}", pa.getId(), pa.getHubCase().getCaseNumber());
        }
        if (!breached.isEmpty()) {
            log.info("PA SLA check: {} breaches found", breached.size());
        }
    }
}
