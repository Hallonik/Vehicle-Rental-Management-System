// src/app/admin/settings/settings.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { AdminSettings } from './settings.model';

const STORAGE_KEY = 'vehicle_rental_admin_settings';

const DEFAULT_SETTINGS: AdminSettings = {
  defaultRentalDays: 3,
  basePricePerDay: 1200,
  lateFeePerDay: 200,
  autoApproveBookings: true,
  cancellationWindowHours: 24
};

@Injectable({
  providedIn: 'root' // or provide it in AdminModule if you prefer
})
export class SettingsService {
  private _settings$ = new BehaviorSubject<AdminSettings>(this.loadInitial());

  private loadInitial(): AdminSettings {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      try { return JSON.parse(raw) as AdminSettings; } catch { /* ignore */ }
    }
    return DEFAULT_SETTINGS;
  }

  getSettings(): Observable<AdminSettings> {
    return this._settings$.asObservable();
  }

  getCurrent(): AdminSettings {
    return this._settings$.getValue();
  }

  updateSettings(newSettings: AdminSettings): Observable<AdminSettings> {
    // simulate HTTP latency
    localStorage.setItem(STORAGE_KEY, JSON.stringify(newSettings));
    this._settings$.next(newSettings);
    return of(newSettings).pipe(delay(300));
  }

  resetToDefaults(): Observable<AdminSettings> {
    localStorage.removeItem(STORAGE_KEY);
    this._settings$.next(DEFAULT_SETTINGS);
    return of(DEFAULT_SETTINGS).pipe(delay(200));
  }
}
