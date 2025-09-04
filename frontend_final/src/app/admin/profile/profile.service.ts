import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Profile } from './profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/auth';

  constructor(private http: HttpClient) {}

  private get headers(): HttpHeaders {
    const token = localStorage.getItem('loginToken');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  /** Fetch logged-in user's profile */
  getProfile(): Observable<Profile> {
    return this.http.get<Profile>(`${this.baseUrl}/profile`, { headers: this.headers });
  }

  /** Update profile with changed fields */
 /** Update profile with changed fields */
updateProfile(patch: Partial<Profile>): Observable<string> {
  return this.http.put(`${this.baseUrl}/updateProfile`, patch, { 
    headers: this.headers, 
    responseType: 'text' 
  }) as Observable<string>;
}

}
