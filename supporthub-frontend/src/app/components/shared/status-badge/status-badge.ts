import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketStatus } from '../../../models/enums';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.html',
  styleUrls: ['./status-badge.scss']
})
export class StatusBadgeComponent {
  @Input() status!: TicketStatus;

  getStatusClass(): string {
    const map: Record<TicketStatus, string> = {
      [TicketStatus.NEW]: 'new',
      [TicketStatus.UNDER_REVIEW]: 'review',
      [TicketStatus.ASSIGNED]: 'assigned',
      [TicketStatus.IN_PROGRESS]: 'progress',
      [TicketStatus.WAITING_FOR_CLIENT]: 'waiting',
      [TicketStatus.RESOLVED]: 'resolved',
      [TicketStatus.UNDER_QA]: 'qa',
      [TicketStatus.VERIFIED]: 'verified',
      [TicketStatus.CLOSED]: 'closed',
      [TicketStatus.REOPENED]: 'reopened'
    };
    return map[this.status] ?? 'new';
  }
}