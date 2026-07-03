import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { TicketCategory, TicketPriority, Environment } from '../../../models/enums';

@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-ticket.html',
  styleUrls: ['./create-ticket.scss']
})
export class CreateTicketComponent {
  form: FormGroup;
  loading = false;
  error = '';
  success = '';
  categories = Object.values(TicketCategory);
  priorities = Object.values(TicketPriority);
  environments = Object.values(Environment);

  constructor(private fb: FormBuilder, private ticketService: TicketService, private router: Router) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      category: [TicketCategory.BUG, Validators.required],
      priority: [TicketPriority.MEDIUM, Validators.required],
      environment: [Environment.PRODUCTION, Validators.required],
      applicationModule: [''],
      version: [''],
      browser: [''],
      stepsToReproduce: [''],
      expectedResult: [''],
      actualResult: ['']
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.ticketService.createTicket(this.form.value).subscribe({
      next: (res) => {
        this.loading = false;
        this.success = 'Ticket ' + res.data.ticketNumber + ' created successfully!';
        setTimeout(() => this.router.navigate(['/client/my-tickets']), 1500);
      },
      error: (err) => { this.loading = false; this.error = err.error?.message || 'Failed to create ticket.'; }
    });
  }
}