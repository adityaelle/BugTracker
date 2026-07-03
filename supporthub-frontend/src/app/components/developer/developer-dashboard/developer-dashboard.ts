import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { AuthService } from '../../../services/auth';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge';
import { Ticket, UpdateStatusRequest } from '../../../models/ticket.model';
import { TicketStatus, UserRole } from '../../../models/enums';

@Component({
  selector: 'app-developer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './developer-dashboard.html',
  styleUrls: ['./developer-dashboard.scss']
})
export class DeveloperDashboardComponent implements OnInit {
  tickets: Ticket[] = [];
  filtered: Ticket[] = [];
  loading = true;
  searchTerm = '';
  selectedStatus = '';
  statuses = Object.values(TicketStatus);
  TicketStatus = TicketStatus;
  isTeamLead = false;

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isTeamLead = this.authService.getRole() === UserRole.TEAM_LEAD;
    this.ticketService.getAssignedTickets().subscribe({
      next: (res) => { this.tickets = res.data; this.filtered = res.data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  applyFilters(): void {
    this.filtered = this.tickets.filter(t => {
      const matchSearch = !this.searchTerm ||
        t.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        t.ticketNumber.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.selectedStatus || t.status === this.selectedStatus;
      return matchSearch && matchStatus;
    });
  }

  updateStatus(ticket: Ticket, status: TicketStatus): void {
    const request: UpdateStatusRequest = { status, remarks: '' };
    this.ticketService.updateStatus(ticket.id, request).subscribe({
      next: (res) => {
        const idx = this.tickets.findIndex(t => t.id === ticket.id);
        if (idx !== -1) this.tickets[idx] = res.data;
        this.applyFilters();
      }
    });
  }

  viewTicket(id: number): void {
    const base = this.isTeamLead ? '/teamlead' : '/developer';
    this.router.navigate([base + '/tickets', id]);
  }
}