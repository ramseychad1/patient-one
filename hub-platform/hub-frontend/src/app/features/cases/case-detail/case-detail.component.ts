import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { CaseDto, TimelineEntryDto, CaseTaskDto, PriorAuthorizationDto, InsurancePlanDto } from '../../../core/models';

@Component({
  selector: 'app-case-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
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

  private stageOrder = ['Referral','Enrollment','BIVB','PA','FinancialAssist','Initiation','Adherence'];

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.api.get<CaseDto>(`/cases/${id}`).subscribe(c => this.caseData = c);
      this.api.get<TimelineEntryDto[]>(`/cases/${id}/timeline`).subscribe(t => this.timeline = t);
      this.api.get<CaseTaskDto[]>(`/cases/${id}/tasks`).subscribe(t => this.tasks = t);
      this.api.get<PriorAuthorizationDto[]>(`/cases/${id}/pa`).subscribe(p => this.paList = p);
      this.api.get<InsurancePlanDto[]>(`/cases/${id}/insurance`).subscribe(i => this.insurancePlans = i);
    }
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
}
