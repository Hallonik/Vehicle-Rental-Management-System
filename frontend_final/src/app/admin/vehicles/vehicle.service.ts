// src/app/admin/vehicle.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map, forkJoin } from 'rxjs';

export interface Vehicle {
  vehicle_id?: number;
  vehicle_type: string;
  vehicle_name: string;
  image_Url: string;
  vehicle_status?: string;
  vehicle_rate_per_hour: number;
  vehicle_hourly_rate_late_fee: number;
  vehicle_availability?: boolean;
  seating_capacity: number;
  fuel_type: string;
  ratings: number;
  ratingCount: number;
  total?: number; // we will populate this separately
}

@Injectable({
  providedIn: 'root'
})
export class VehicleService {

  private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Fetch all vehicles and merge with counts
  getVehicles(): Observable<Vehicle[]> {
    const headers = this.getAuthHeaders();

    const allVehicles$ = this.http.get<Vehicle[]>(`${this.baseUrl}/allVehicles`, { headers });
    const counts$ = this.http.get<any[]>(`${this.baseUrl}/countByTypeAndName`, { headers });

    return forkJoin([allVehicles$, counts$]).pipe(
      map(([vehicles, counts]) => {
        return vehicles.map(v => {
          const match = counts.find(c => 
            c.vehicleName === v.vehicle_name && c.vehicleType === v.vehicle_type
          );
          return {
            ...v,
            total: match ? match.count : 0
          };
        });
      })
    );
  }

  addVehicle(vehicle: Vehicle): Observable<Vehicle> {
    const headers = this.getAuthHeaders();
    return this.http.post<Vehicle>(`${this.baseUrl}/add`, vehicle, { headers });
  }

  deleteVehicle(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/deleteVehicle/${id}`, { headers });
  }
}
