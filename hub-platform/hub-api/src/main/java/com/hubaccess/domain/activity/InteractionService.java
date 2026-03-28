package com.hubaccess.domain.activity;

import com.hubaccess.domain.activity.dto.InteractionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final InteractionRepository interactionRepository;

    @Transactional(readOnly = true)
    public List<InteractionDto> getByCaseId(UUID caseId) {
        return interactionRepository.findByHubCaseIdOrderByInteractionAtDesc(caseId).stream()
            .map(this::toDto).toList();
    }

    private InteractionDto toDto(Interaction i) {
        return InteractionDto.builder()
            .id(i.getId())
            .caseId(i.getHubCase().getId())
            .interactionType(i.getInteractionType())
            .direction(i.getDirection())
            .channel(i.getChannel())
            .summary(i.getSummary())
            .notes(i.getNotes())
            .documentReference(i.getDocumentReference())
            .performedBy(i.getPerformedBy() != null ? i.getPerformedBy().getId() : null)
            .performedByName(i.getPerformedBy() != null ? i.getPerformedBy().getFirstName() + " " + i.getPerformedBy().getLastName() : null)
            .interactionAt(i.getInteractionAt())
            .build();
    }
}
