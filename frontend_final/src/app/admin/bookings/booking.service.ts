// src/app/admin/bookings/booking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking } from './booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private apiUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/getall';
  private monthlyUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/monthly';
  private vehicleApiUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles';


  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  /** ✅ Fetch all bookings */
  getBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  /** ✅ Fetch monthly stats */
  getMonthlyBookings(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(this.monthlyUrl, { headers: this.getAuthHeaders() });
  }

  /** ✅ Delete a booking */
  deleteBooking(bookingId: number): Observable<{ success: boolean; message: string; bookingId: number }> {
    return this.http.delete<{ success: boolean; message: string; bookingId: number }>(
      `http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/delete/${bookingId}`,
      { headers: this.getAuthHeaders() }
    );
  }

  /** ✅ Fetch payment status counts for donut chart */
getPaymentStatusCount(): Observable<{ [key: string]: number }> {
  return this.http.get<{ [key: string]: number }>(
    'http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/statusCount',
    { headers: this.getAuthHeaders() }
  );
}


// ✅ New method for updating vehicle status
  updateVehicleStatus(vehicleId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    console.log("Vehicle:", vehicleId);

    const body = {
      vehicle_availability: true,
      vehicle_status: 'AVAILABLE'
    };


    return this.http.patch<any>(`${this.vehicleApiUrl}/updateStatus/${vehicleId}`, body, { headers });
  }
}
