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

  // User form
  showUserForm = false;
  editingUser: HubUserDto | null = null;
  newUser = { email: '', firstName: '', lastName: '', password: '', roleName: 'CaseManager' };
  roles = ['HubAdmin', 'Supervisor', 'CaseManager', 'ManufacturerViewer'];

  // Program assignment
  showProgramAssign: HubUserDto | null = null;
  selectedProgramIds: Set<string> = new Set();
  allProgramsSelected = false;

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

  startEditUser(user: HubUserDto): void {
    this.editingUser = { ...user };
  }

  saveUser(): void {
    if (!this.editingUser) return;
    this.api.put<HubUserDto>(`/users/${this.editingUser.id}`, {
      firstName: this.editingUser.firstName,
      lastName: this.editingUser.lastName,
      status: this.editingUser.status
    }).subscribe(() => {
      this.editingUser = null;
      this.loadUsers();
    });
  }

  cancelEditUser(): void {
    this.editingUser = null;
  }

  deleteUser(user: HubUserDto): void {
    if (!confirm(`Delete user ${user.firstName} ${user.lastName}?`)) return;
    this.api.delete(`/users/${user.id}`).subscribe({
      next: () => this.loadUsers(),
      error: () => this.loadUsers()
    });
  }

  openProgramAssign(user: HubUserDto): void {
    this.showProgramAssign = user;
    this.selectedProgramIds = new Set(user.programIds || []);
    this.allProgramsSelected = this.programs.length > 0 &&
      this.programs.every(p => this.selectedProgramIds.has(p.id));
  }

  closeProgramAssign(): void {
    this.showProgramAssign = null;
    this.selectedProgramIds = new Set();
    this.allProgramsSelected = false;
  }

  toggleProgram(programId: string): void {
    if (this.selectedProgramIds.has(programId)) {
      this.selectedProgramIds.delete(programId);
    } else {
      this.selectedProgramIds.add(programId);
    }
    this.allProgramsSelected = this.programs.every(p => this.selectedProgramIds.has(p.id));
  }

  toggleAllPrograms(): void {
    if (this.allProgramsSelected) {
      this.selectedProgramIds.clear();
      this.allProgramsSelected = false;
    } else {
      this.programs.forEach(p => this.selectedProgramIds.add(p.id));
      this.allProgramsSelected = true;
    }
  }

  saveProgramAssignments(): void {
    if (!this.showProgramAssign) return;
    const programIds = Array.from(this.selectedProgramIds);
    this.api.post(`/users/${this.showProgramAssign.id}/programs`, {
      programIds,
      accessLevel: 'ReadWrite'
    }).subscribe(() => {
      this.closeProgramAssign();
      this.loadUsers();
    });
  }

  isAdmin(user: HubUserDto): boolean {
    return user.roles?.includes('HubAdmin');
  }
}
