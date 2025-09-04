import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Profile } from './profile.model';
import { ProfileService } from './profile.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  saving = false;
  editMode = false;
  profile?: Profile;
  private originalSnapshot?: Profile;
  selectedImageFile?: File;
  imagePreviewUrl?: string;

  private PHONE_RE = /^[6-9]\d{9}$/;

  constructor(
    private fb: FormBuilder,
    private svc: ProfileService,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      fullName: ['', Validators.required],
      email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
      userName: [{ value: '', disabled: true }],
      phone: ['', [Validators.pattern(this.PHONE_RE)]],
      drivingLicenseNumber: [''],
      paymentMethod: [''],
      imageUrl: ['']
    });

    this.load();
  }

  load(): void {
    this.loading = true;
    this.svc.getProfile().subscribe({
      next: p => {
        this.profile = p;
        this.originalSnapshot = JSON.parse(JSON.stringify(p));
        this.patchForm(p);
        this.imagePreviewUrl = p.imageUrl || '';
        this.form.disable();
        this.form.get('email')?.disable();
        this.loading = false;
      },
      error: err => {
        console.error('Failed to load profile', err);
        this.snack.open('Failed to load profile', 'Close', { duration: 4000 });
        this.loading = false;
      }
    });
  }

  private patchForm(p: Profile) {
    this.form.patchValue({
      fullName: p.fullName ?? '',
      email: p.email ?? '',
      userName: p.userName ?? '',
      phone: p.phone ?? '',
      drivingLicenseNumber: p.drivingLicenseNumber ?? '',
      paymentMethod: p.paymentMethod ?? '',
      imageUrl: p.imageUrl ?? ''
    });
  }

  enterEdit(): void {
    this.editMode = true;
    ['fullName', 'phone', 'drivingLicenseNumber', 'paymentMethod', 'imageUrl'].forEach(k =>
      this.form.get(k)?.enable()
    );
  }

  cancelEdit(): void {
    if (this.originalSnapshot) {
      this.patchForm(this.originalSnapshot);
      this.imagePreviewUrl = this.originalSnapshot.imageUrl || '';
      this.selectedImageFile = undefined;
    }
    this.editMode = false;
    this.form.markAsPristine();
    this.form.updateValueAndValidity();
    this.form.disable();
    this.form.get('email')?.disable();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedImageFile = input.files[0];
      const reader = new FileReader();
      reader.onload = e => {
        this.imagePreviewUrl = e.target?.result as string;
        this.form.get('imageUrl')?.setValue(this.imagePreviewUrl);
      };
      reader.readAsDataURL(this.selectedImageFile);
    }
  }

  save(): void {
  if (!this.editMode) return;
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    this.snack.open('Please fix validation errors before saving', 'Close', { duration: 3500 });
    return;
  }

  if (!this.profile) {
    this.snack.open('Profile not loaded', 'Close', { duration: 3000 });
    return;
  }

  // Merge original profile with form values (keep imageUrl unchanged)
  const updatedProfile: Profile = {
    ...this.profile,
    ...this.form.getRawValue(),
    imageUrl: this.profile.imageUrl   // preserve
  };

  this.saving = true;
  this.svc.updateProfile(updatedProfile).subscribe({
    next: (msg: string) => {
      console.log('Backend message:', msg);

      // Since backend didn’t return updated profile, we just update local state manually
      this.profile = updatedProfile;
      this.originalSnapshot = JSON.parse(JSON.stringify(updatedProfile));
      this.patchForm(updatedProfile);
      this.imagePreviewUrl = updatedProfile.imageUrl || '';

      this.selectedImageFile = undefined;
      this.form.disable();
      this.form.get('email')?.disable();
      this.editMode = false;
      this.saving = false;

      this.snack.open(msg, 'Close', { duration: 3000 });  // use backend message
    },
    error: (err) => {
      console.error('Update failed', err);
      this.saving = false;
      this.snack.open('Failed to save profile', 'Close', { duration: 4000 });
    }
  });
}


  initials(name?: string) {
    if (!name) return '';
    return name.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase();
  }
}
