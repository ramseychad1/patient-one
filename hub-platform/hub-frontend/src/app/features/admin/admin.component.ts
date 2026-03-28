import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { ManufacturerDto, ProgramDto, HubUserDto, Page } from '../../core/models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent implements OnInit {
  activeTab = 'manufacturers';
  manufacturers: ManufacturerDto[] = [];
  programs: ProgramDto[] = [];
  users: HubUserDto[] = [];

  // New manufacturer form
  showMfrForm = false;
  newMfr = { name: '', primaryContactName: '', primaryContactEmail: '' };

  // New program form
  showProgramForm = false;
  newProgram = { manufacturerId: '', name: '', drugBrandName: '', therapeuticArea: '' };

  // New user form
  showUserForm = false;
  newUser = { email: '', firstName: '', lastName: '', password: '', roleName: 'CaseManager' };
  roles = ['HubAdmin', 'Supervisor', 'CaseManager', 'ManufacturerViewer'];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadManufacturers();
    this.loadPrograms();
    this.loadUsers();
  }

  loadManufacturers(): void {
    this.api.get<ManufacturerDto[]>('/manufacturers').subscribe(m => this.manufacturers = m);
  }

  loadPrograms(): void {
    this.api.get<ProgramDto[]>('/programs').subscribe(p => this.programs = p);
  }

  loadUsers(): void {
    this.api.get<Page<HubUserDto>>('/users', { page: 0, size: 100 }).subscribe(p => this.users = p.content);
  }

  setTab(tab: string): void { this.activeTab = tab; }

  createManufacturer(): void {
    this.api.post<ManufacturerDto>('/manufacturers', this.newMfr).subscribe(() => {
      this.showMfrForm = false;
      this.newMfr = { name: '', primaryContactName: '', primaryContactEmail: '' };
      this.loadManufacturers();
    });
  }

  createProgram(): void {
    this.api.post<ProgramDto>('/programs', this.newProgram).subscribe(() => {
      this.showProgramForm = false;
      this.newProgram = { manufacturerId: '', name: '', drugBrandName: '', therapeuticArea: '' };
      this.loadPrograms();
    });
  }

  userError = '';

  createUser(): void {
    this.userError = '';
    this.api.post<HubUserDto>('/users/invite', this.newUser).subscribe({
      next: () => {
        this.showUserForm = false;
        this.newUser = { email: '', firstName: '', lastName: '', password: '', roleName: 'CaseManager' };
        this.loadUsers();
      },
      error: (err) => {
        this.userError = err.error?.error?.message || err.message || 'Failed to create user';
      }
    });
  }
}
