import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { User, UserRole } from './users-management.model';
import { UserService } from './users-management.servics';

type DialogData = { mode: 'create' | 'edit'; user?: User };

@Component({
  selector: 'app-user-dialog',
    standalone: false,
  templateUrl: './user-dialog.component.html',
  styleUrl: './user-dialog.component.scss'
})
export class UserDialogComponent {
  form: FormGroup;
  title = 'Add User';
  roles: UserRole[] = ['ADMIN', 'CUSTOMER'];
  saving = false;

  constructor(
    private fb: FormBuilder,
    private svc: UserService,
    private snack: MatSnackBar,
    private ref: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    const u = data.user || { name: '', email: '', phone: '', role: 'CUSTOMER', active: true } as User;
    this.title = data.mode === 'edit' ? 'Edit User' : 'Add User';
    this.form = this.fb.group({
      name: [u.name, [Validators.required, Validators.minLength(2)]],
      email: [u.email, [Validators.required, Validators.email]],
      phone: [u.phone],
      role: [u.role, Validators.required],
      active: [u.active]
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.saving = true;

    const payload: User = { ...this.data.user, ...this.form.value };

    const req$ = this.data.mode === 'edit' && this.data.user?.id
      ? this.svc.update(this.data.user.id, payload)
      : this.svc.create(payload);

    req$.subscribe({
      next: () => {
        this.snack.open('Saved', 'OK', { duration: 1500 });
        this.ref.close(true);
      },
      error: () => {
        this.snack.open('Failed to save user', 'OK', { duration: 2000 });
        this.saving = false;
      }
    });
  }
}
