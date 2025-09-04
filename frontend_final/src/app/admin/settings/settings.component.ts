// src/app/admin/settings/settings.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SettingsService } from './settings.service';
import { AdminSettings } from './settings.model';

@Component({
  selector: 'app-admin-settings',
  standalone: false,
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  form!: FormGroup;
  currentSettings: AdminSettings | null = null;

  saving = false;
  message = '';

  constructor(private fb: FormBuilder, private settingsSvc: SettingsService) {}

  ngOnInit(): void {
    this.buildForm();

    this.settingsSvc.getSettings().subscribe(s => {
      this.currentSettings = s;
      // patch form values (safe if form not created yet)
      if (this.form) {
        this.form.patchValue({
          defaultRentalDays: s.defaultRentalDays,
          basePricePerDay: s.basePricePerDay,
          lateFeePerDay: s.lateFeePerDay,
          autoApproveBookings: s.autoApproveBookings,
          cancellationWindowHours: s.cancellationWindowHours
        });
      }
    });
  }

  private buildForm() {
    this.form = this.fb.group({
      defaultRentalDays: [3, [Validators.required, Validators.min(1)]],
      basePricePerDay: [100, [Validators.required, Validators.min(0)]],
      lateFeePerDay: [100, [Validators.required, Validators.min(0)]],
      autoApproveBookings: [false],
      cancellationWindowHours: [24, [Validators.required, Validators.min(0)]]
    });
  }

  save() {
    if (this.form.invalid) {
      this.message = 'Please fix form errors before saving';
      return;
    }
    this.saving = true;
    this.message = '';
    const payload: AdminSettings = {
      defaultRentalDays: Number(this.form.value.defaultRentalDays),
      basePricePerDay: Number(this.form.value.basePricePerDay),
      lateFeePerDay: Number(this.form.value.lateFeePerDay),
      autoApproveBookings: !!this.form.value.autoApproveBookings,
      cancellationWindowHours: Number(this.form.value.cancellationWindowHours)
    };

    this.settingsSvc.updateSettings(payload).subscribe({
      next: (res) => {
        this.currentSettings = res;
        this.saving = false;
        this.message = 'Settings saved successfully.';
      },
      error: () => {
        this.saving = false;
        this.message = 'Failed to save settings.';
      }
    });
  }

  resetToDefaults() {
    this.settingsSvc.resetToDefaults().subscribe(def => {
      this.form.patchValue(def);
      this.currentSettings = def;
      this.message = 'Reset to defaults.';
    });
  }
}
