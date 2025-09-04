import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_URLS } from './Constants';

interface AuthResponse {
  roles: string[];
  userName: string;
  token: string;
  userId:string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private token: string | null = null;
    private userId: string | null = null;

  // Headers without token (login, signup)
  private headersWithoutToken = new HttpHeaders({
    'Content-Type': 'application/json',
  });


  constructor(private http: HttpClient) {}

  // ---------- AUTH METHODS ----------

  signIn(userName: string, password: string): Observable<AuthResponse> {
    const body = { userName, password};
    return this.http.post<AuthResponse>(
      `${API_URLS.BASE_URL}${API_URLS.LOGIN}`,
      body,
      { headers: this.headersWithoutToken }
    ).pipe(
      tap((res: any) => this.storeAuthData(res.token, res.userName, res.roles,res.userId))
    );
  }

  signUp(
    name: string,
    email: string,
    mobileNumber: string,
    password: string,
    userName: string,
    role: string,
    otp: string
  ): Observable<AuthResponse> {
    const body = { name, email, mobileNumber, password, userName, role,otp };
    return this.http.post<AuthResponse>(
      `${API_URLS.BASE_URL}${API_URLS.SIGNUP}`,
      body,
      { headers: this.headersWithoutToken }
    ).pipe(
      tap(res => this.storeAuthData(res.token, res.userName, res.roles,res.userId))
    );
  }

  forgotPassword(username: string, newPassword: string): Observable<any> {
    return this.http.post(
      `${API_URLS.BASE_URL}${API_URLS.FORGOTPASSWORD}`,
      { userName: username, password: newPassword },
      { headers: this.headersWithoutToken }
    );
  }

  


  // ---------- TOKEN-BASED REQUEST HELPERS ----------

  postWithToken(endpoint: string, body: any): Observable<any> {
    return this.http.post(
      `${API_URLS.BASE_URL}${endpoint}`,
      body,
      { headers: this.getAuthHeaders() }
    );
  }

  postWithTokenstring(endpoint: string, body: any): Observable<any> {
  const finalUrl = endpoint.startsWith('http')
    ? endpoint
    : `${API_URLS.BASE_URL}${endpoint}`;
  return this.http.post(finalUrl, body, { headers: this.getAuthHeaders() });
}

  getWithToken(endpoint: string): Observable<any> {
    return this.http.get(
      `${API_URLS.BASE_URL}${endpoint}`,
      { headers: this.getAuthHeaders() }
    );
  }

  // ---------- TOKEN HANDLING ----------

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.getToken()}`
    });
  }

  getToken(): string | null {
    return this.token || localStorage.getItem('authToken');
  }

   getUserRole(): string | null {
    return localStorage.getItem('userRoles');
  }

  setToken(token: string): void {
    this.token = token;
    localStorage.setItem('authToken', token);
  }
    getUserId(): string | null {
    return this.userId || localStorage.getItem('userId');
  }

  
  clearToken(): void {
    this.token = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    localStorage.removeItem('userRoles');
  }

  private storeAuthData(token: string, username: string, roles: string[],userId: string): void {
    localStorage.setItem('authToken', token);
    localStorage.setItem('username', username);
    localStorage.setItem('userId', userId.toString());
    localStorage.setItem('userRoles', JSON.stringify(roles));
    this.token = token;
  }

    public clearStorage(): void {
      localStorage.clear();
  }


    public sendOtp(email:string,name: string){
       const body = {  email,name };
    return this.http.post<AuthResponse>(
      `${API_URLS.BASE_URL}${API_URLS.SENDOTP}`,
      body,
      { headers: this.headersWithoutToken }
    )
    }

    getWithoutToken(url: string) {
  return this.http.get(url);  // no Authorization header
}

getWithTokenNew(endpoint: string): Observable<any> {
  const finalUrl = endpoint.startsWith('http')
    ? endpoint
    : `${API_URLS.BASE_URL}${endpoint}`;
  return this.http.get(finalUrl, { headers: this.getAuthHeaders() });
}

postWithTokenNew(endpoint: string, body: any): Observable<any> {
  const finalUrl = endpoint.startsWith('http')
    ? endpoint
    : `${API_URLS.BASE_URL}${endpoint}`;
  return this.http.post(finalUrl, body, { headers: this.getAuthHeaders() });
}




patchWithToken(endpoint: string, body: any): Observable<any> {
  const finalUrl = endpoint.startsWith('http')
    ? endpoint
    : `${API_URLS.BASE_URL}${endpoint}`;
  return this.http.patch(finalUrl, body, { headers: this.getAuthHeaders() });
}

patchWithTokenNew(url: string, body: any) {
  const token = localStorage.getItem('authToken') || '';
  return this.http.patch(url, body, {
    headers: { Authorization: `Bearer ${token}` }
  });
}



// AuthService.ts
updatePaymentStatus(bookingId: number, status: string): Observable<any> {
  const token = localStorage.getItem('authToken'); // fetch Bearer token
  return this.http.patch(
    `http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/updatePaymentStatus/${bookingId}?status=${status}`,
    {},
    { headers: { Authorization: `Bearer ${token}` } }
  );
}

// ✅ Get paymentId by bookingId
getPaymentIdByBookingIdnew(bookingId: number) {
  const url = `http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess/getPaymentIdByBookingId/${bookingId}`;
   return this.http.get(url, { headers: this.getAuthHeaders() });
}


// ✅ Update booking with paymentId
setPaymentIdForBooking(bookingId: number, paymentId: number) {
  const url = `http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/setPaymentId/${bookingId}?paymentId=${paymentId}`;
  return this.patchWithToken(url, {}); // body not needed, but send empty {}
}


getWithTokenCustomHeader(endpoint: string, token: string): Observable<any> {
  const finalUrl = endpoint.startsWith('http')
    ? endpoint
    : `${API_URLS.BASE_URL}${endpoint}`;

  return this.http.get(finalUrl, {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    })
  });
}


refundPayment(paymentId: number, amount: number): Observable<any> {
    const body = { paymentId, amount };
    const url = `http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess/RefundByTransactionId`;
    return this.http.post(url, body, { headers: this.getAuthHeadersNew() });
  }

  // ✅ 2. Update Vehicle Status
  updateVehicleStatus(vehicleId: number, status: string, availability: boolean): Observable<any> {
    const body = {
      vehicle_status: status,
      vehicle_availability: availability.toString()
    };
    const url = `http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/updateStatus/${vehicleId}`;
    return this.http.patch(url, body, { headers: this.getAuthHeadersNew() });
  }

  // ✅ 3. Update Booking Payment Status
  updateBookingPaymentStatus(bookingId: number, status: string): Observable<any> {
    const url = `http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/updatePaymentStatus/${bookingId}?status=${status}`;
    return this.http.patch(url, {}, { headers: this.getAuthHeadersNew() });
  }

  // ✅ 4. Get Auth Headers (common)
  private getAuthHeadersNew(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.getTokenCustomized()}`
    });
  }

  getTokenCustomized(): string | null {
    return this.token || localStorage.getItem('authToken');
  }



  

}
