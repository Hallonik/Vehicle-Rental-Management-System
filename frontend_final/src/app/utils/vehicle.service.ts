import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Vehicle {
  vehicleId: number;
  vehicleType: string;
  vehicleName: string;
  vehicleRatePerHour: number;
  vehicleHourlyRateLateFee: number;
  vehicleAvailability: boolean;
  vehicleStatus: string;
  seatingCapacity: number;
  manufacturer: string;
  yearOfManufacture: number;
  ratings: string;
  ratingCount: number;
}

export interface ResultDTO<T> {
  data: T;
  statusCode: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  private apiUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles';

  constructor(private http: HttpClient) {}

   getVehicles(): Observable<Vehicle[]> {
  return this.http.get<ResultDTO<Vehicle[]>>(this.apiUrl)
    .pipe(
      map(res => res.data)
    );
}


}
