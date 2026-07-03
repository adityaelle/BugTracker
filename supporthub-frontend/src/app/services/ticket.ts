import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ApiResponse,
  AssignTicketRequest,
  AddCommentRequest,
  CreateTicketRequest,
  DashboardResponse,
  Ticket,
  TicketComment,
  TicketDetail,
  UpdateStatusRequest
} from '../models/ticket.model';
import { User } from '../models/user.model';
import { UserRole } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class TicketService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  createTicket(request: CreateTicketRequest): Observable<ApiResponse<Ticket>> {
    return this.http.post<ApiResponse<Ticket>>(`${this.baseUrl}/tickets`, request);
  }

  getMyTickets(): Observable<ApiResponse<Ticket[]>> {
    return this.http.get<ApiResponse<Ticket[]>>(`${this.baseUrl}/tickets/my`);
  }

  getAssignedTickets(): Observable<ApiResponse<Ticket[]>> {
    return this.http.get<ApiResponse<Ticket[]>>(`${this.baseUrl}/tickets/assigned`);
  }

  getAllTickets(): Observable<ApiResponse<Ticket[]>> {
    return this.http.get<ApiResponse<Ticket[]>>(`${this.baseUrl}/tickets`);
  }

  getTicketDetail(ticketId: number): Observable<ApiResponse<TicketDetail>> {
    return this.http.get<ApiResponse<TicketDetail>>(`${this.baseUrl}/tickets/${ticketId}`);
  }

  assignTicket(ticketId: number, request: AssignTicketRequest): Observable<ApiResponse<Ticket>> {
    return this.http.patch<ApiResponse<Ticket>>(
      `${this.baseUrl}/tickets/${ticketId}/assign`, request);
  }

  updateStatus(ticketId: number, request: UpdateStatusRequest): Observable<ApiResponse<Ticket>> {
    return this.http.patch<ApiResponse<Ticket>>(
      `${this.baseUrl}/tickets/${ticketId}/status`, request);
  }

  qaVerdict(ticketId: number, request: UpdateStatusRequest): Observable<ApiResponse<Ticket>> {
    return this.http.patch<ApiResponse<Ticket>>(
      `${this.baseUrl}/tickets/${ticketId}/qa-verdict`, request);
  }

  closeTicket(ticketId: number): Observable<ApiResponse<Ticket>> {
    return this.http.patch<ApiResponse<Ticket>>(
      `${this.baseUrl}/tickets/${ticketId}/close`, {});
  }

  reopenTicket(ticketId: number, request: AddCommentRequest): Observable<ApiResponse<Ticket>> {
    return this.http.patch<ApiResponse<Ticket>>(
      `${this.baseUrl}/tickets/${ticketId}/reopen`, request);
  }

  addComment(ticketId: number, request: AddCommentRequest): Observable<ApiResponse<TicketComment>> {
    return this.http.post<ApiResponse<TicketComment>>(
      `${this.baseUrl}/tickets/${ticketId}/comments`, request);
  }

  getDashboard(): Observable<ApiResponse<DashboardResponse>> {
    return this.http.get<ApiResponse<DashboardResponse>>(`${this.baseUrl}/tickets/dashboard`);
  }

  getUsersByRole(role: UserRole): Observable<ApiResponse<User[]>> {
    return this.http.get<ApiResponse<User[]>>(`${this.baseUrl}/admin/users/role/${role}`);
  }

  getAllUsers(): Observable<ApiResponse<User[]>> {
    return this.http.get<ApiResponse<User[]>>(`${this.baseUrl}/admin/users`);
  }

  toggleUserActive(userId: number): Observable<ApiResponse<User>> {
    return this.http.patch<ApiResponse<User>>(
      `${this.baseUrl}/admin/users/${userId}/toggle-active`, {});
  }
  getAssignableUsers(): Observable<ApiResponse<User[]>> {
  return this.http.get<ApiResponse<User[]>>(`${this.baseUrl}/tickets/assignable-users`);
}
getTicketsUnderQa(): Observable<ApiResponse<Ticket[]>> {
  return this.http.get<ApiResponse<Ticket[]>>(`${this.baseUrl}/tickets/under-qa`);
}
}