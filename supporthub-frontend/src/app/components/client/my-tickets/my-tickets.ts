import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge';
import { Ticket } from '../../../models/ticket.model';
import { TicketStatus, TicketCategory } from '../../../models/enums';

@Component({
  selector: 'app-my-tickets',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe, StatusBadgeComponent],
  templateUrl: './my-tickets.html',
  styleUrls: ['./my-tickets.scss']
})
export class MyTicketsComponent implements OnInit {
  tickets: Ticket[] = [];
  filtered: Ticket[] = [];
  loading = true;
  searchTerm = '';
  selectedStatus = '';
  selectedCategory = '';
  statuses = Object.values(TicketStatus);
  categories = Object.values(TicketCategory);

  constructor(private ticketService: TicketService, private router: Router) {}

  ngOnInit(): void {
    this.ticketService.getMyTickets().subscribe({
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
      const matchCategory = !this.selectedCategory || t.category === this.selectedCategory;
      return matchSearch && matchStatus && matchCategory;
    });
  }

  viewTicket(id: number): void { this.router.navigate(['/client/tickets', id]); }
}