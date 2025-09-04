import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultDTO, Payment, PaymentModeCounts } from './reports.model';

@Injectable({
  providedIn: 'root'
})
export class RevenueReportService {
  private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  // 🔹 Get all payments
  getAllPayments(): Observable<ResultDTO<Payment[]>> {
    return this.http.get<ResultDTO<Payment[]>>(`${this.baseUrl}/AllPayments`, {
      headers: this.getAuthHeaders()
    });
  }

  // 🔹 Delete payment by ID
  deletePayment(paymentId: number): Observable<ResultDTO<string>> {
    return this.http.delete<ResultDTO<string>>(`${this.baseUrl}/delete/${paymentId}`, {
      headers: this.getAuthHeaders()
    });
  }

  // 🔹 Get payment counts by mode
  getCountByMode(): Observable<ResultDTO<PaymentModeCounts>> {
    return this.http.get<ResultDTO<PaymentModeCounts>>(`${this.baseUrl}/countByMode`, {
      headers: this.getAuthHeaders()
    });
  }
}
