export type UserRole = 'ADMIN' | 'CUSTOMER';

export interface User {
  id?: number;
  name: string;
  email: string;
  phone?: string;
  role: UserRole;
  active: boolean;
  createdAt?: string; // optional, if backend provides it
}


