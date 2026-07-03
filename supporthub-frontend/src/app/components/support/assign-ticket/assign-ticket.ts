import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { Ticket } from '../../../models/ticket.model';
import { User } from '../../../models/user.model';
import { TicketPriority, TicketSeverity, UserRole } from '../../../models/enums';

@Component({
  selector: 'app-assign-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './assign-ticket.html',
  styleUrls: ['./assign-ticket.scss']
})
export class AssignTicketComponent implements OnInit {
  ticket: Ticket | null = null;
  developers: User[] = [];
  form: FormGroup;
  loading = false;
  error = '';
  success = '';
  priorities = Object.values(TicketPriority);
  severities = Object.values(TicketSeverity);
  ticketId!: number;

  constructor(
    private fb: FormBuilder,
    private ticketService: TicketService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      assigneeId: ['', Validators.required],
      priority: [TicketPriority.MEDIUM, Validators.required],
      severity: [TicketSeverity.MEDIUM, Validators.required],
      remarks: ['']
    });
  }

 ngOnInit(): void {
  this.ticketId = Number(this.route.snapshot.paramMap.get('id'));
  this.ticketService.getTicketDetail(this.ticketId).subscribe({
    next: (res) => { this.ticket = res.data.ticket; }
  });
  this.ticketService.getAssignableUsers().subscribe({
    next: (res) => { this.developers = res.data; }
  });
} 

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.ticketService.assignTicket(this.ticketId, this.form.value).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Ticket assigned successfully!';
        setTimeout(() => this.router.navigate(['/support/dashboard']), 1500);
      },
      error: (err) => { this.loading = false; this.error = err.error?.message || 'Assignment failed.'; }
    });
  }
}