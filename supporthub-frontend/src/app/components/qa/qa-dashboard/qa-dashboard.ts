import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { Ticket, UpdateStatusRequest } from '../../../models/ticket.model';
import { TicketStatus } from '../../../models/enums';

@Component({
  selector: 'app-qa-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './qa-dashboard.html',
  styleUrls: ['./qa-dashboard.scss']
})
export class QaDashboardComponent implements OnInit {
  tickets: Ticket[] = [];
  filtered: Ticket[] = [];
  loading = true;
  searchTerm = '';
  TicketStatus = TicketStatus;

  constructor(private ticketService: TicketService, private router: Router) {}

  ngOnInit(): void {
  this.ticketService.getTicketsUnderQa().subscribe({
    next: (res) => {
      this.tickets = res.data;
      this.filtered = res.data;
      this.loading = false;
    },
    error: () => { this.loading = false; }
  });
}
  

  applyFilters(): void {
    this.filtered = this.tickets.filter(t =>
      !this.searchTerm ||
      t.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      t.ticketNumber.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  pass(ticket: Ticket): void {
    const request: UpdateStatusRequest = { status: TicketStatus.VERIFIED, remarks: 'QA Passed' };
    this.ticketService.qaVerdict(ticket.id, request).subscribe({
      next: () => { this.tickets = this.tickets.filter(t => t.id !== ticket.id); this.applyFilters(); }
    });
  }

  fail(ticket: Ticket): void {
    const remarks = prompt('Reason for failure:') || 'QA Failed';
    const request: UpdateStatusRequest = { status: TicketStatus.IN_PROGRESS, remarks };
    this.ticketService.qaVerdict(ticket.id, request).subscribe({
      next: () => { this.tickets = this.tickets.filter(t => t.id !== ticket.id); this.applyFilters(); }
    });
  }

  viewTicket(id: number): void { this.router.navigate(['/qa/tickets', id]); }
}