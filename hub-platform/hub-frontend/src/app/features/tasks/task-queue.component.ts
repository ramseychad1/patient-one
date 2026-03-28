import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { CaseTaskDto, Page } from '../../core/models';

@Component({
  selector: 'app-task-queue',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './task-queue.component.html',
  styleUrl: './task-queue.component.scss'
})
export class TaskQueueComponent implements OnInit {
  tasks: CaseTaskDto[] = [];
  activeFilter = 'All';
  filters = ['All', 'Open', 'InProgress'];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<Page<CaseTaskDto>>('/tasks/mine', { page: 0, size: 100 }).subscribe(page => {
      this.tasks = page.content;
    });
  }

  setFilter(f: string): void { this.activeFilter = f; }

  get filteredTasks(): CaseTaskDto[] {
    if (this.activeFilter === 'All') return this.tasks;
    return this.tasks.filter(t => t.status === this.activeFilter);
  }

  getPriorityClass(p: string): string {
    return p === 'Urgent' ? 'urgent' : p === 'High' ? 'warn' : 'neutral';
  }

  isOverdue(task: CaseTaskDto): boolean {
    if (!task.dueDate) return false;
    return new Date(task.dueDate) < new Date();
  }
}
