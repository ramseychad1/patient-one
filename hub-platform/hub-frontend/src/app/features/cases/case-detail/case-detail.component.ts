import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { CaseDto, TimelineEntryDto, CaseTaskDto, PriorAuthorizationDto, InsurancePlanDto, HubUserDto, Page, stageLabel } from '../../../core/models';

@Component({
  selector: 'app-case-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './case-detail.component.html',
  styleUrl: './case-detail.component.scss'
})
export class CaseDetailComponent implements OnInit {
  caseData: CaseDto | null = null;
  timeline: TimelineEntryDto[] = [];
  tasks: CaseTaskDto[] = [];
  paList: PriorAuthorizationDto[] = [];
  insurancePlans: InsurancePlanDto[] = [];
  activeTab = 'activity';
  stageLabel = stageLabel;

  // Edit case
  editing = false;
  editForm = { stage: '', priority: '', consentStatus: '', notes: '' };
  stages = ['Referral','Enrollment','BIVB','PA','FinancialAssist','Initiation','Adherence','Closed'];
  priorities = ['Normal', 'High', 'Urgent'];
  consentStatuses = ['Pending', 'Sent', 'Received', 'Expired'];

  // Create PA
  showPaForm = false;
  newPa = { submissionMethod: 'Portal', clinicalNotes: '' };
  paSubmissionMethods = ['Portal', 'Fax', 'Phone', 'Electronic'];

  private stageOrder = ['Referral','Enrollment','BIVB','PA','FinancialAssist','Initiation','Adherence'];
  private caseId = '';

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.caseId = this.route.snapshot.paramMap.get('id') || '';
    if (this.caseId) {
      this.loadCase();
      this.api.get<TimelineEntryDto[]>(`/cases/${this.caseId}/timeline`).subscribe(t => this.timeline = t);
      this.api.get<CaseTaskDto[]>(`/cases/${this.caseId}/tasks`).subscribe(t => this.tasks = t);
      this.api.get<PriorAuthorizationDto[]>(`/cases/${this.caseId}/pa`).subscribe(p => this.paList = p);
      this.api.get<InsurancePlanDto[]>(`/cases/${this.caseId}/insurance`).subscribe(i => this.insurancePlans = i);
    }
  }

  loadCase(): void {
    this.api.get<CaseDto>(`/cases/${this.caseId}`).subscribe(c => this.caseData = c);
  }

  setTab(tab: string): void {
    this.activeTab = tab;
  }

  getStagePillClass(stage: string): string {
    const map: Record<string, string> = {
      'Referral': 'info', 'Enrollment': 'info', 'BIVB': 'warn',
      'PA': 'warn', 'FinancialAssist': 'warn', 'Initiation': 'ok',
      'Adherence': 'ok', 'Closed': 'neutral'
    };
    return map[stage] || 'neutral';
  }

  isStageCompleted(stage: string): boolean {
    if (!this.caseData) return false;
    const current = this.stageOrder.indexOf(this.caseData.stage);
    const check = this.stageOrder.indexOf(stage);
    return check < current;
  }

  // Edit case
  startEdit(): void {
    if (!this.caseData) return;
    this.editForm = {
      stage: this.caseData.stage,
      priority: this.caseData.priority,
      consentStatus: this.caseData.consentStatus,
      notes: this.caseData.notes || ''
    };
    this.editing = true;
  }

  cancelEdit(): void {
    this.editing = false;
  }

  saveEdit(): void {
    if (!this.caseData) return;
    this.api.patch<CaseDto>(`/cases/${this.caseId}`, this.editForm).subscribe({
      next: (c) => {
        this.caseData = c;
        this.editing = false;
        // Refresh timeline to show status change
        this.api.get<TimelineEntryDto[]>(`/cases/${this.caseId}/timeline`).subscribe(t => this.timeline = t);
      },
      error: () => this.editing = false
    });
  }

  // Task status updates
  updateTaskStatus(task: CaseTaskDto, newStatus: string): void {
    this.api.patch<CaseTaskDto>(`/cases/${this.caseId}/tasks/${task.id}`, { status: newStatus }).subscribe(updated => {
      const idx = this.tasks.findIndex(t => t.id === task.id);
      if (idx >= 0) this.tasks[idx] = updated;
    });
  }

  isOverdue(task: CaseTaskDto): boolean {
    if (!task.dueDate || task.status === 'Completed') return false;
    return new Date(task.dueDate) < new Date();
  }

  // Create PA
  openPaForm(): void {
    this.newPa = { submissionMethod: 'Portal', clinicalNotes: '' };
    this.showPaForm = true;
  }

  cancelPaForm(): void {
    this.showPaForm = false;
  }

  createPa(): void {
    this.api.post<PriorAuthorizationDto>(`/cases/${this.caseId}/pa`, this.newPa).subscribe(pa => {
      this.paList.push(pa);
      this.showPaForm = false;
    });
  }

  // Timeline display helpers
  formatSummary(entry: TimelineEntryDto): string {
    if (entry.type === 'StatusChange' && entry.summary) {
      return entry.summary.replace(/^null/, 'New');
    }
    return entry.summary;
  }
}
