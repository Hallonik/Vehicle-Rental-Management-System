export type PaymentStatus = 'SUCCESSFUL' | 'UNSUCCESSFUL';

export interface Booking {
  bookingId: number;
  userId: number;
  name: string;
  vehicleId: number;
  vehicleName: string;
  duration: number;
  bookingDate: string; // ISO date string
  location: string;
  amount: number;
  paymentStatus: PaymentStatus;
}
