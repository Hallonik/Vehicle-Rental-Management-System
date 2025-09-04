export interface Maintenance {
  maintenanceId: number;
  vehicleId: number;
  vehicleType: string;
  vehicleName: string;
  serviceDate: Date;
  cost: number;
  description?: string;
}
