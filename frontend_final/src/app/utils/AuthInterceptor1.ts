// src/app/utils/AuthInterceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService1 } from './AuthService1';
import { API_URLS1 } from './Constants1';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService1) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // ✅ Public endpoints that do NOT require token
    const publicEndpoints = [
      `${API_URLS1.BASE_URL}${API_URLS1.SIGNUP}`,       // Register
      `${API_URLS1.BASE_URL}${API_URLS1.LOGIN}`,       // Login
      `${API_URLS1.BASE_URL}/send-otp`,               // Send OTP
      `${API_URLS1.BASE_URL}/verify-otp`              // Verify OTP
    ];

    // ✅ If request URL matches a public endpoint → skip token
    if (publicEndpoints.some(url => req.url.includes(url))) {
      return next.handle(req);
    }

    // ✅ Get token from AuthService
    const token = this.authService.getToken();

    if (token) {
      // ✅ Clone request and attach Authorization header
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next.handle(authReq);
    }

    // ✅ If no token → send original request
    return next.handle(req);
  }
}
