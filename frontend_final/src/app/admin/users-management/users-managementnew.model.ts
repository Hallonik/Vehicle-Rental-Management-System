export type UserRole = 'ADMIN' | 'CUSTOMER';
export type UserStatus = 'ACTIVE' | 'BLOCKED';

export interface User {
  userId: number;                // maps backend field
  fullName: string;              // backend sends fullName
  email: string;
  phone?: string;
  drivingLicenseNumber?: string | null;
  paymentMethod?: string | null;
  userName: string;
  status: UserStatus;            // ACTIVE / BLOCKED
  role: UserRole;                // ADMIN / CUSTOMER
  createdAt?: string;            // optional timestamp (if backend sends)
}
