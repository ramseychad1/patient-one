import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/services/api.service';
import { CaseDto, Page } from '../../../core/models';

@Component({
  selector: 'app-cases-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './cases-list.component.html',
  styleUrl: './cases-list.component.scss'
})
export class CasesListComponent implements OnInit {
  cases: CaseDto[] = [];
  totalCases = 0;
  searchTerm = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadCases();
  }

  loadCases(): void {
    this.api.get<Page<CaseDto>>('/cases', { page: 0, size: 100 }).subscribe(page => {
      this.cases = page.content;
      this.totalCases = page.totalElements;
    });
  }

  get filteredCases(): CaseDto[] {
    if (!this.searchTerm) return this.cases;
    const term = this.searchTerm.toLowerCase();
    return this.cases.filter(c =>
      c.caseNumber.toLowerCase().includes(term) ||
      c.patient?.firstName.toLowerCase().includes(term) ||
      c.patient?.lastName.toLowerCase().includes(term) ||
      c.programName.toLowerCase().includes(term)
    );
  }

  getStagePillClass(stage: string): string {
    const map: Record<string, string> = {
      'Referral': 'info', 'Enrollment': 'info', 'BIVB': 'warn',
      'PA': 'warn', 'FinancialAssist': 'warn', 'Initiation': 'ok',
      'Adherence': 'ok', 'Closed': 'neutral'
    };
    return map[stage] || 'neutral';
  }
}
