// src/app/utils/AuthService.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService1 {
  private baseUrl = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/auth';

  constructor(private http: HttpClient) {}

  // ✅ Sign up (registration)
  

  // ✅ Send OTP
  sendOtp(payload: { fullName: string; email: string; phone: string; password: string; role: string }): Observable<any> {
  return this.http.post(`${this.baseUrl}/register`, payload, { responseType: 'text' as 'json' });
}

  // ✅ Verify OTP
  verifyOtp(email: string, otp: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/verify-otp`, { email, otp },{ responseType: 'text' as 'json' });
  }

  // ✅ Login
  loginIn(payload: { email: string; password: string, role: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, payload,{ responseType: 'text' as 'json' });
  }


  // ✅ Forgot Password - Send OTP
  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/forgot-password`, { email }, { responseType: 'text' });
  }

  // ✅ Forgot Password - Verify OTP
  verifyForgotOtp(email: string, otp: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/verify-forgot-otp`, { email, otp },{responseType: 'text'  });
  }

  // ✅ Forgot Password - Reset
  resetPassword(token: string, newPassword: string, confirmPassword: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/reset-password`,
      { newPassword, confirmPassword },
      { headers: { Authorization: `Bearer ${token}` }, responseType: 'text'  }
    );
  }


  // ✅ Save token to local storage
  saveToken(token: string) {
    localStorage.setItem('loginToken', token);
  }

  // ✅ Get token from local storage
  getToken(): string | null {
    return localStorage.getItem('loginToken');
  }

  // ✅ Remove token from local storage (Logout)
  logout() {
    localStorage.removeItem('loginToken');
  }

  
}
