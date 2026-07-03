import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge';
import { Ticket, DashboardResponse } from '../../../models/ticket.model';
import { TicketStatus, TicketPriority } from '../../../models/enums';

@Component({
  selector: 'app-support-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './support-dashboard.html',
  styleUrls: ['./support-dashboard.scss']
})
export class SupportDashboardComponent implements OnInit {
  tickets: Ticket[] = [];
  filtered: Ticket[] = [];
  dashboard: DashboardResponse | null = null;
  loading = true;
  searchTerm = '';
  selectedStatus = '';
  selectedPriority = '';
  statuses = Object.values(TicketStatus);
  priorities = Object.values(TicketPriority);

  constructor(private ticketService: TicketService, private router: Router) {}

  ngOnInit(): void {
    this.ticketService.getAllTickets().subscribe({
      next: (res) => { this.tickets = res.data; this.filtered = res.data; this.loading = false; },
      error: () => { this.loading = false; }
    });
    this.ticketService.getDashboard().subscribe({
      next: (res) => { this.dashboard = res.data; }
    });
  }

  applyFilters(): void {
    this.filtered = this.tickets.filter(t => {
      const matchSearch = !this.searchTerm ||
        t.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        t.ticketNumber.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.selectedStatus || t.status === this.selectedStatus;
      const matchPriority = !this.selectedPriority || t.priority === this.selectedPriority;
      return matchSearch && matchStatus && matchPriority;
    });
  }

  viewTicket(id: number): void { this.router.navigate(['/support/tickets', id]); }
  assignTicket(id: number): void { this.router.navigate(['/support/tickets', id, 'assign']); }
}