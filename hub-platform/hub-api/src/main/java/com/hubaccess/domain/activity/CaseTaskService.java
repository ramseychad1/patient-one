package com.hubaccess.domain.activity;

import com.hubaccess.domain.activity.dto.*;
import com.hubaccess.domain.auth.HubUser;
import com.hubaccess.domain.auth.HubUserRepository;
import com.hubaccess.domain.auth.UserProgramAssignmentRepository;
import com.hubaccess.domain.cases.CaseRepository;
import com.hubaccess.domain.cases.HubCase;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseTaskService {

    private final CaseTaskRepository taskRepository;
    private final CaseRepository caseRepository;
    private final HubUserRepository userRepository;
    private final UserProgramAssignmentRepository programAssignmentRepository;

    @Transactional(readOnly = true)
    public List<CaseTaskDto> getTasksByCase(UUID caseId) {
        return taskRepository.findByHubCaseId(caseId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<CaseTaskDto> getMyTasks(Pageable pageable) {
        UUID userId = getCurrentUserId();
        boolean isAdmin = isCurrentUserAdmin();

        Page<CaseTask> tasks;
        if (isAdmin) {
            tasks = taskRepository.findOpenTasksByAssignedTo(userId, pageable);
        } else {
            List<UUID> programIds = programAssignmentRepository.findActiveProgamIdsByUserId(userId);
            tasks = taskRepository.findOpenTasksByAssignedToAndProgramIds(userId, programIds, pageable);
        }
        return tasks.map(this::toDto);
    }

    @Transactional
    public CaseTaskDto createTask(UUID caseId, CreateTaskRequest request) {
        HubCase hubCase = caseRepository.findById(caseId)
            .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        CaseTask task = CaseTask.builder()
            .hubCase(hubCase)
            .taskType(request.getTaskType())
            .title(request.getTitle())
            .priority(request.getPriority())
            .dueDate(request.getDueDate())
            .build();

        if (request.getAssignedTo() != null) {
            HubUser assignee = userRepository.findById(request.getAssignedTo())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignedTo(assignee);
        }
        if (request.getNotes() != null) task.setNotes(request.getNotes());

        try {
            HubUser currentUser = userRepository.findById(getCurrentUserId()).orElse(null);
            task.setCreatedByUser(currentUser);
        } catch (Exception ignored) {}

        return toDto(taskRepository.save(task));
    }

    @Transactional
    public CaseTaskDto updateTask(UUID taskId, UpdateTaskRequest request) {
        CaseTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if ("Completed".equals(request.getStatus())) {
                task.setCompletedAt(Instant.now());
                try {
                    HubUser currentUser = userRepository.findById(getCurrentUserId()).orElse(null);
                    task.setCompletedBy(currentUser);
                } catch (Exception ignored) {}
            }
        }
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getNotes() != null) task.setNotes(request.getNotes());
        if (request.getAssignedTo() != null) {
            HubUser assignee = userRepository.findById(request.getAssignedTo())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignedTo(assignee);
        }

        return toDto(taskRepository.save(task));
    }

    private CaseTaskDto toDto(CaseTask t) {
        return CaseTaskDto.builder()
            .id(t.getId())
            .caseId(t.getHubCase().getId())
            .caseNumber(t.getHubCase().getCaseNumber())
            .patientName(t.getHubCase().getPatient().getFirstName() + " " + t.getHubCase().getPatient().getLastName())
            .programName(t.getHubCase().getProgram().getName())
            .taskType(t.getTaskType())
            .title(t.getTitle())
            .status(t.getStatus())
            .priority(t.getPriority())
            .dueDate(t.getDueDate())
            .assignedTo(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
            .assignedToName(t.getAssignedTo() != null ? t.getAssignedTo().getFirstName() + " " + t.getAssignedTo().getLastName() : null)
            .completedAt(t.getCompletedAt())
            .notes(t.getNotes())
            .autoGenerated(t.getAutoGenerated())
            .createdAt(t.getCreatedAt())
            .build();
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(auth.getName());
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HubAdmin"));
    }
}
