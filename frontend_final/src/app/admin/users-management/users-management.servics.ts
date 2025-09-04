import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { User } from './users-management.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly api = 'http://hallonik.ap-south-1.elasticbeanstalk.com/api/users';
  private readonly useMock = true; // Toggle between mock and real API

 private readonly mockUsers: User[] = [
  { id: 1, name: 'Alice Johnson', email: 'alice@example.com', phone: '9876543210', active: true, role: 'ADMIN' },
  { id: 2, name: 'Bob Smith', email: 'bob@example.com', active: false, role: 'CUSTOMER' },
  { id: 3, name: 'Charlie Brown', email: 'charlie@example.com', phone: '9123456780', active: true, role: 'CUSTOMER' },
  { id: 4, name: 'Diana Prince', email: 'diana@example.com', phone: '9988776655', active: true, role: 'ADMIN' },
  { id: 5, name: 'Ethan Hunt', email: 'ethan@example.com', active: false, role: 'CUSTOMER' },
  { id: 6, name: 'Fiona Gallagher', email: 'fiona@example.com', phone: '9012345678', active: true, role: 'CUSTOMER' },
  { id: 7, name: 'George Clooney', email: 'george@example.com', active: true, role: 'CUSTOMER' },
  { id: 8, name: 'Hannah Baker', email: 'hannah@example.com', phone: '9234567890', active: false, role: 'CUSTOMER' },
  { id: 9, name: 'Ian Somerhalder', email: 'ian@example.com', active: true, role: 'ADMIN' },
  { id: 10, name: 'Julia Roberts', email: 'julia@example.com', phone: '9345678901', active: true, role: 'CUSTOMER' },
  { id: 11, name: 'Kevin Hart', email: 'kevin@example.com', active: false, role: 'CUSTOMER' },
  { id: 12, name: 'Laura Palmer', email: 'laura@example.com', phone: '9456789012', active: true, role: 'CUSTOMER' }
];



  constructor(private http: HttpClient) {}

  list(): Observable<User[]> {
    return this.useMock ? of(this.mockUsers) : this.http.get<User[]>(this.api);
  }

  get(id: number): Observable<User> {
    if (this.useMock) {
      const found = this.mockUsers.find(u => u.id === id);
      return of(found!);
    }
    return this.http.get<User>(`${this.api}/${id}`);
  }

  create(payload: User): Observable<User> {
    if (this.useMock) {
      const newUser = { ...payload, id: Date.now() };
      this.mockUsers.push(newUser);
      return of(newUser);
    }
    return this.http.post<User>(this.api, payload);
  }

  update(id: number, payload: User): Observable<User> {
    if (this.useMock) {
      const idx = this.mockUsers.findIndex(u => u.id === id);
      if (idx > -1) {
        this.mockUsers[idx] = { ...payload, id };
      }
      return of(this.mockUsers[idx]);
    }
    return this.http.put<User>(`${this.api}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    if (this.useMock) {
      const idx = this.mockUsers.findIndex(u => u.id === id);
      if (idx > -1) this.mockUsers.splice(idx, 1);
      return of(void 0);
    }
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  toggleStatus(id: number): Observable<User> {
    if (this.useMock) {
      const user = this.mockUsers.find(u => u.id === id);
      if (user) user.active = !user.active;
      return of(user!);
    }
    return this.http.put<User>(`${this.api}/${id}/toggle-status`, {});
  }
}
