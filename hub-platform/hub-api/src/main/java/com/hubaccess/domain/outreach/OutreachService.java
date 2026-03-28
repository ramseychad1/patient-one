package com.hubaccess.domain.outreach;

import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.auth.HubUserRepository;
import com.hubaccess.domain.cases.CaseRepository;
import com.hubaccess.domain.cases.HubCase;
import com.hubaccess.domain.outreach.dto.CreateOutreachRequest;
import com.hubaccess.domain.outreach.dto.OutreachDto;
import com.hubaccess.outreach.SmsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutreachService {

    private final OutreachRepository outreachRepository;
    private final CaseRepository caseRepository;
    private final HubUserRepository userRepository;
    private final SmsService smsService;

    @Transactional(readOnly = true)
    public List<OutreachDto> getByCaseId(UUID caseId) {
        return outreachRepository.findByHubCaseId(caseId).stream().map(this::toDto).toList();
    }

    @Transactional
    public OutreachDto send(UUID caseId, CreateOutreachRequest request) {
        HubCase hubCase = caseRepository.findById(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        String uniqueUrl = "https://hub.example.com/r/" + UUID.randomUUID().toString().substring(0, 8);
        String accessCode = String.format("%06d", (int)(Math.random() * 999999));

        HubUser sender = null;
        try {
            sender = userRepository.findById(getCurrentUserId()).orElse(null);
        } catch (Exception ignored) {}

        PatientOutreach outreach = PatientOutreach.builder()
            .hubCase(hubCase)
            .outreachType(request.getOutreachType())
            .phoneNumber(request.getPhoneNumber())
            .sentAt(Instant.now())
            .sentBy(sender)
            .uniqueUrl(uniqueUrl)
            .accessCode(accessCode)
            .urlExpiresAt(Instant.now().plusSeconds(7 * 24 * 3600))
            .messageBody(request.getMessageBody())
            .build();

        outreach = outreachRepository.save(outreach);

        // Send SMS
        smsService.send(request.getPhoneNumber(), request.getMessageBody(), uniqueUrl, accessCode);

        return toDto(outreach);
    }

    private OutreachDto toDto(PatientOutreach o) {
        return OutreachDto.builder()
            .id(o.getId()).caseId(o.getHubCase().getId())
            .outreachType(o.getOutreachType()).channel(o.getChannel())
            .phoneNumber(o.getPhoneNumber()).sentAt(o.getSentAt())
            .sentBy(o.getSentBy() != null ? o.getSentBy().getId() : null)
            .uniqueUrl(o.getUniqueUrl()).accessCode(o.getAccessCode())
            .urlExpiresAt(o.getUrlExpiresAt()).responded(o.getResponded())
            .respondedAt(o.getRespondedAt()).responseChannel(o.getResponseChannel())
            .attemptNumber(o.getAttemptNumber()).messageBody(o.getMessageBody())
            .build();
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(auth.getName());
    }
}
