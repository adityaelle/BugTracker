import { Routes } from '@angular/router';
import { UserRole } from './models/enums';
import { AuthGuard } from './guards/auth-guard';
import { RoleGuard } from './guards/role-guard';

import { LoginComponent } from './components/auth/login/login';
import { RegisterComponent } from './components/auth/register/register';
import { TicketDetailComponent } from './components/shared/ticket-detail/ticket-detail';
import { ClientDashboardComponent } from './components/client/client-dashboard/client-dashboard';
import { CreateTicketComponent } from './components/client/create-ticket/create-ticket';
import { MyTicketsComponent } from './components/client/my-tickets/my-tickets';
import { SupportDashboardComponent } from './components/support/support-dashboard/support-dashboard';
import { AssignTicketComponent } from './components/support/assign-ticket/assign-ticket';
import { DeveloperDashboardComponent } from './components/developer/developer-dashboard/developer-dashboard';
import { QaDashboardComponent } from './components/qa/qa-dashboard/qa-dashboard';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'client',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.CLIENT] },
    children: [
      { path: 'dashboard', component: ClientDashboardComponent },
      { path: 'create-ticket', component: CreateTicketComponent },
      { path: 'my-tickets', component: MyTicketsComponent },
      { path: 'tickets/:id', component: TicketDetailComponent }
    ]
  },
  {
    path: 'support',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.SUPPORT_DESK] },
    children: [
      { path: 'dashboard', component: SupportDashboardComponent },
      { path: 'tickets/:id', component: TicketDetailComponent },
      { path: 'tickets/:id/assign', component: AssignTicketComponent }
    ]
  },
  {
    path: 'developer',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.DEVELOPER] },
    children: [
      { path: 'dashboard', component: DeveloperDashboardComponent },
      { path: 'tickets/:id', component: TicketDetailComponent }
    ]
  },
  {
    path: 'qa',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.QA_TESTER] },
    children: [
      { path: 'dashboard', component: QaDashboardComponent },
      { path: 'tickets/:id', component: TicketDetailComponent }
    ]
  },
  {
    path: 'teamlead',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TEAM_LEAD] },
    children: [
      { path: 'dashboard', component: DeveloperDashboardComponent },
      { path: 'tickets/:id', component: TicketDetailComponent }
    ]
  },
  {
    path: 'admin',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.ADMIN] },
    children: [
      { path: 'dashboard', component: AdminDashboardComponent }
    ]
  },
  { path: '**', redirectTo: '/login' }
];