import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { User } from './users-managementnew.model';

@Injectable({ providedIn: 'root' })
export class UserService {
   private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/auth';

  constructor(private http: HttpClient) {}

  /** Generic search API */
  search(keyword: string): Observable<User[]> {
    return this.http.post<User[]>(`${this.baseUrl}/search`, { keyword });
  }

  /** Toggle user status */
  toggleStatus(userId: number): Observable<string> {
    return this.http.put<string>(`${this.baseUrl}/users/${userId}/toggle-status`, {},{ responseType: 'text' as 'json' });
  }

  /** Delete user by ID */
  delete(userId: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/deleteuser/${userId}`, { responseType: 'text' as 'json' });
  }

  mapApiUser(u: any): User {
  return {
    userId: u.user_id,
    fullName: u.full_name,
    email: u.email,
    userName: u.user_name,
    phone: u.phone,
    drivingLicenseNumber: u.driving_license_number,
    role: u.role,
    status: u.status
  };
}
}
