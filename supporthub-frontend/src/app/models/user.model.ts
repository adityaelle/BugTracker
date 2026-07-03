import { Department, UserRole } from './enums';

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  department: Department;
  isActive: boolean;
}

export interface AuthResponse {
  token: string;
  email: string;
  fullName: string;
  role: UserRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  role: UserRole;
  department?: Department;
}