import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { DashboardResponse } from '../../../models/ticket.model';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './client-dashboard.html',
  styleUrls: ['./client-dashboard.scss']
})
export class ClientDashboardComponent implements OnInit {
  dashboard: DashboardResponse | null = null;
  loading = true;

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.ticketService.getDashboard().subscribe({
      next: (res) => { this.dashboard = res.data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }
}