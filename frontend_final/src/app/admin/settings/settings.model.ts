// src/app/admin/settings/settings.model.ts
export interface AdminSettings {
  defaultRentalDays: number;
  basePricePerDay: number;
  lateFeePerDay: number;
  autoApproveBookings: boolean;
  cancellationWindowHours: number;
}
