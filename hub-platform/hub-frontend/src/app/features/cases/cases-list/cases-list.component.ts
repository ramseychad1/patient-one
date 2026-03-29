import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { CaseDto, PatientDto, ProgramDto, Page, stageLabel } from '../../../core/models';

@Component({
  selector: 'app-cases-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './cases-list.component.html',
  styleUrl: './cases-list.component.scss'
})
export class CasesListComponent implements OnInit {
  cases: CaseDto[] = [];
  programs: ProgramDto[] = [];
  totalCases = 0;
  searchTerm = '';
  activeStageFilter = 'All';
  stages = ['All', 'Referral', 'Enrollment', 'BIVB', 'PA', 'FinancialAssist', 'Initiation', 'Adherence', 'Closed'];
  stageLabel = stageLabel;

  // Create case form
  showCreateForm = false;
  newCase = {
    programId: '',
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    enrollmentSource: 'Portal',
    priority: 'Normal'
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadCases();
    this.api.get<ProgramDto[]>('/programs').subscribe(p => this.programs = p);
  }

  loadCases(): void {
    this.api.get<Page<CaseDto>>('/cases', { page: 0, size: 100 }).subscribe(page => {
      this.cases = page.content;
      this.totalCases = page.totalElements;
    });
  }

  get filteredCases(): CaseDto[] {
    let result = this.cases;
    if (this.activeStageFilter !== 'All') {
      result = result.filter(c => c.stage === this.activeStageFilter);
    }
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(c =>
        c.caseNumber.toLowerCase().includes(term) ||
        c.patient?.firstName.toLowerCase().includes(term) ||
        c.patient?.lastName.toLowerCase().includes(term) ||
        c.programName.toLowerCase().includes(term)
      );
    }
    return result;
  }

  filterByStage(stage: string): void {
    this.activeStageFilter = stage;
  }

  getStagePillClass(stage: string): string {
    const map: Record<string, string> = {
      'Referral': 'info', 'Enrollment': 'info', 'BIVB': 'warn',
      'PA': 'warn', 'FinancialAssist': 'warn', 'Initiation': 'ok',
      'Adherence': 'ok', 'Closed': 'neutral'
    };
    return map[stage] || 'neutral';
  }

  openCreateForm(): void {
    this.newCase = { programId: '', firstName: '', lastName: '', dateOfBirth: '', enrollmentSource: 'Portal', priority: 'Normal' };
    this.showCreateForm = true;
  }

  cancelCreate(): void {
    this.showCreateForm = false;
  }

  createCase(): void {
    this.api.post<CaseDto>('/cases', this.newCase).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.loadCases();
      },
      error: () => {}
    });
  }
}
