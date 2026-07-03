import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketService } from '../../../services/ticket';
import { AuthService } from '../../../services/auth';
import { StatusBadgeComponent } from '../status-badge/status-badge';
import { TicketDetail } from '../../../models/ticket.model';
import { TicketStatus, UserRole } from '../../../models/enums';

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, StatusBadgeComponent],
  templateUrl: './ticket-detail.html',
  styleUrls: ['./ticket-detail.scss']
})
export class TicketDetailComponent implements OnInit {
  detail: TicketDetail | null = null;
  loading = true;
  commentText = '';
  reopenReason = '';
  showReopenForm = false;
  UserRole = UserRole;
  TicketStatus = TicketStatus;
  ticketId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ticketService: TicketService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.ticketId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadDetail();
  }

  loadDetail(): void {
    this.ticketService.getTicketDetail(this.ticketId).subscribe({
      next: (res) => { this.detail = res.data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  addComment(): void {
    if (!this.commentText.trim()) return;
    this.ticketService.addComment(this.ticketId, { content: this.commentText }).subscribe({
      next: () => { this.commentText = ''; this.loadDetail(); }
    });
  }

  closeTicket(): void {
    this.ticketService.closeTicket(this.ticketId).subscribe({
      next: () => this.loadDetail()
    });
  }

  reopenTicket(): void {
    if (!this.reopenReason.trim()) return;
    this.ticketService.reopenTicket(this.ticketId, { content: this.reopenReason }).subscribe({
      next: () => { this.showReopenForm = false; this.reopenReason = ''; this.loadDetail(); }
    });
  }

  goBack(): void { this.router.navigate(['../..'], { relativeTo: this.route }); }
}