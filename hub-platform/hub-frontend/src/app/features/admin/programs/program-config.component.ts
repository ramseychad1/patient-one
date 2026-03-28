import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { ProgramConfigDto, ProgramDto } from '../../../core/models';

@Component({
  selector: 'app-program-config',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './program-config.component.html',
  styleUrl: './program-config.component.scss'
})
export class ProgramConfigComponent implements OnInit {
  program: ProgramDto | null = null;
  config: ProgramConfigDto | null = null;
  saving = false;

  constructor(private api: ApiService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.api.get<ProgramDto>(`/programs/${id}`).subscribe(p => this.program = p);
      this.api.get<ProgramConfigDto>(`/programs/${id}/config`).subscribe(c => this.config = c);
    }
  }

  save(): void {
    if (!this.config || !this.program) return;
    this.saving = true;
    this.api.put<ProgramConfigDto>(`/programs/${this.program.id}/config`, this.config).subscribe({
      next: (c) => { this.config = c; this.saving = false; },
      error: () => this.saving = false
    });
  }
}
