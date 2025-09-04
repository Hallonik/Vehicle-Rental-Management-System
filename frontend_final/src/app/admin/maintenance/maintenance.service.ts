import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Maintenance } from './maintenance.model';

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/maintenances';
  private vehicleUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles';

  constructor(private http: HttpClient) {}

  private get headers(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  getAll(): Observable<Maintenance[]> {
    return this.http.get<Maintenance[]>(`${this.baseUrl}/get`, { headers: this.headers });
  }

  add(payload: any): Observable<Maintenance> {
    return this.http.post<Maintenance>(`${this.baseUrl}/add`, payload, { headers: this.headers });
  }

  remove(id: number): Observable<any> {
  return this.http.delete(`${this.baseUrl}/delete/${id}`, {
    headers: this.headers,
    responseType: 'text'
  });
}

  getVehicleTypeAndName(vehicleId: string): Observable<any> {
    return this.http.get<any>(`${this.vehicleUrl}/${vehicleId}/type-name`, { headers: this.headers });
  }

  updateVehicleStatus(vehicleId: number, status: string, availability: boolean): Observable<any> {
  const body = {
    vehicle_status: status,
    vehicle_availability: availability
  };

  return this.http.patch(`${this.vehicleUrl}/updateStatus/${vehicleId}`, body, {
    headers: this.headers
  });
}
}
