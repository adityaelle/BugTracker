import { Environment, TicketCategory, TicketPriority, TicketSeverity, TicketStatus } from './enums';
import { User } from './user.model';

export interface Ticket {
  id: number;
  ticketNumber: string;
  title: string;
  description: string;
  category: TicketCategory;
  priority: TicketPriority;
  severity: TicketSeverity;
  status: TicketStatus;
  applicationModule: string;
  environment: Environment;
  version: string;
  browser: string;
  stepsToReproduce: string;
  expectedResult: string;
  actualResult: string;
  raisedBy: User;
  assignedTo: User;
  slaDeadline: string;
  slaBreached: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TicketComment {
  id: number;
  content: string;
  commentedBy: User;
  createdAt: string;
}

export interface TicketStatusHistory {
  id: number;
  oldStatus: TicketStatus;
  newStatus: TicketStatus;
  remarks: string;
  changedBy: User;
  changedAt: string;
}

export interface Attachment {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadedBy: User;
  createdAt: string;
}

export interface TicketDetail {
  ticket: Ticket;
  comments: TicketComment[];
  statusHistory: TicketStatusHistory[];
  attachments: Attachment[];
}

export interface CreateTicketRequest {
  title: string;
  description: string;
  category: TicketCategory;
  priority: TicketPriority;
  environment: Environment;
  applicationModule?: string;
  version?: string;
  browser?: string;
  stepsToReproduce?: string;
  expectedResult?: string;
  actualResult?: string;
}

export interface AssignTicketRequest {
  assigneeId: number;
  priority: TicketPriority;
  severity: TicketSeverity;
  remarks?: string;
}

export interface UpdateStatusRequest {
  status: TicketStatus;
  remarks?: string;
}

export interface AddCommentRequest {
  content: string;
}

export interface DashboardResponse {
  totalTickets: number;
  newTickets: number;
  openTickets: number;
  inProgressTickets: number;
  resolvedTickets: number;
  closedTickets: number;
  reopenedTickets: number;
  slaBreachedTickets: number;
  bugCount: number;
  enhancementCount: number;
  featureRequestCount: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}