export interface Profile {
  userId: number;
  fullName: string;
  email: string;
  phone?: string;
  drivingLicenseNumber?: string | null;
  paymentMethod?: string | null;
  userName: string;
  status: 'ACTIVE' | 'BLOCKED';
  role: 'ADMIN' | 'CUSTOMER';
  imageUrl?: string | null; // optional, in case backend adds profile pic later
}
