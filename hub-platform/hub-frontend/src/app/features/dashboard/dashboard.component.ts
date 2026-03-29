import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { CaseDto, CaseTaskDto, Page, stageLabel } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  cases: CaseDto[] = [];
  tasks: CaseTaskDto[] = [];
  totalCases = 0;
  urgentCount = 0;
  slaBreachCount = 0;
  pendingConsentCount = 0;
  activeFilter = 'All';
  stages = ['All', 'Referral', 'Enrollment', 'BIVB', 'PA', 'FinancialAssist', 'Initiation', 'Adherence'];
  stageLabel = stageLabel;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadCases();
  }

  loadCases(): void {
    this.api.get<Page<CaseDto>>('/cases', { page: 0, size: 50 }).subscribe(page => {
      this.cases = page.content;
      this.totalCases = page.totalElements;
      this.urgentCount = this.cases.filter(c => c.priority === 'Urgent').length;
      this.slaBreachCount = this.cases.filter(c => c.slaBreachFlag).length;
      this.pendingConsentCount = this.cases.filter(c => c.consentStatus === 'Pending').length;
    });
  }

  filterByStage(stage: string): void {
    this.activeFilter = stage;
  }

  get filteredCases(): CaseDto[] {
    if (this.activeFilter === 'All') return this.cases;
    return this.cases.filter(c => c.stage === this.activeFilter);
  }

  getStagePillClass(stage: string): string {
    const map: Record<string, string> = {
      'Referral': 'info', 'Enrollment': 'info', 'BIVB': 'warn',
      'PA': 'warn', 'FinancialAssist': 'warn', 'Initiation': 'ok',
      'Adherence': 'ok', 'Closed': 'neutral'
    };
    return map[stage] || 'neutral';
  }

  getPriorityClass(priority: string): string {
    return priority === 'Urgent' ? 'urgent' : priority === 'High' ? 'warn' : 'neutral';
  }
}
