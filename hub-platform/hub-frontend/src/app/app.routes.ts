import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent) },
  {
    path: '',
    loadComponent: () => import('./layouts/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'cases', loadComponent: () => import('./features/cases/cases-list/cases-list.component').then(m => m.CasesListComponent) },
      { path: 'cases/:id', loadComponent: () => import('./features/cases/case-detail/case-detail.component').then(m => m.CaseDetailComponent) },
      { path: 'tasks', loadComponent: () => import('./features/tasks/task-queue.component').then(m => m.TaskQueueComponent) },
      { path: 'admin', loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent) },
      { path: 'admin/programs/:id/config', loadComponent: () => import('./features/admin/programs/program-config.component').then(m => m.ProgramConfigComponent) },
    ]
  },
  { path: '**', redirectTo: '' }
];
