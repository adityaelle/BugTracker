import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TicketService } from '../../../services/ticket';
import { AuthService } from '../../../services/auth';
import { User } from '../../../models/user.model';
import { Department, UserRole } from '../../../models/enums';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.scss']
})
export class AdminDashboardComponent implements OnInit {
  users: User[] = [];
  loading = true;
  showCreateForm = false;
  createForm: FormGroup;
  createError = '';
  createSuccess = '';
  roles = Object.values(UserRole);
  departments = Object.values(Department);

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.createForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: [UserRole.DEVELOPER, Validators.required],
      department: [Department.BACKEND]
    });
  }

  ngOnInit(): void { this.loadUsers(); }

  loadUsers(): void {
    this.ticketService.getAllUsers().subscribe({
      next: (res) => { this.users = res.data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  toggleActive(user: User): void {
    this.ticketService.toggleUserActive(user.id).subscribe({
      next: (res) => {
        const idx = this.users.findIndex(u => u.id === user.id);
        if (idx !== -1) this.users[idx] = res.data;
      }
    });
  }

  onCreateUser(): void {
    if (this.createForm.invalid) return;
    this.createError = '';
    this.authService.register(this.createForm.value).subscribe({
      next: () => {
        this.createSuccess = 'User created successfully!';
        this.createForm.reset();
        this.showCreateForm = false;
        this.loadUsers();
      },
      error: (err) => { this.createError = err.error?.message || 'Failed to create user.'; }
    });
  }
}