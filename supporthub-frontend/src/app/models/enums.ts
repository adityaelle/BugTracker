export enum UserRole {
  CLIENT = 'CLIENT',
  SUPPORT_DESK = 'SUPPORT_DESK',
  DEVELOPER = 'DEVELOPER',
  QA_TESTER = 'QA_TESTER',
  TEAM_LEAD = 'TEAM_LEAD',
  ADMIN = 'ADMIN'
}

export enum TicketCategory {
  BUG = 'BUG',
  ENHANCEMENT = 'ENHANCEMENT',
  FEATURE_REQUEST = 'FEATURE_REQUEST'
}

export enum TicketPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum TicketSeverity {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum TicketStatus {
  NEW = 'NEW',
  UNDER_REVIEW = 'UNDER_REVIEW',
  ASSIGNED = 'ASSIGNED',
  IN_PROGRESS = 'IN_PROGRESS',
  WAITING_FOR_CLIENT = 'WAITING_FOR_CLIENT',
  RESOLVED = 'RESOLVED',
  UNDER_QA = 'UNDER_QA',
  VERIFIED = 'VERIFIED',
  CLOSED = 'CLOSED',
  REOPENED = 'REOPENED'
}

export enum Environment {
  PRODUCTION = 'PRODUCTION',
  UAT = 'UAT',
  TEST = 'TEST'
}

export enum Department {
  FRONTEND = 'FRONTEND',
  BACKEND = 'BACKEND',
  MOBILE = 'MOBILE',
  API = 'API',
  DATABASE = 'DATABASE',
  QA = 'QA',
  DEVOPS = 'DEVOPS',
  GENERAL = 'GENERAL'
}