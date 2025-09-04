import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Feedback } from './feedback.model';

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/feedback';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('authToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  list$(): Observable<Feedback[]> {
    return this.http.get<any>(`${this.baseUrl}/getall`, { headers: this.getHeaders() })
      .pipe(map(res => res.data as Feedback[]));
  }

  remove(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${id}`, { headers: this.getHeaders() });
  }
}
