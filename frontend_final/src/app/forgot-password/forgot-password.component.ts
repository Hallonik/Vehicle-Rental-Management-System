import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService1 } from '../utils/AuthService1';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
  imports: [FormsModule, CommonModule]
})
export class ForgotPasswordComponent {
  email: string = '';
  otp: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  otpSent: boolean = false;
  otpVerified: boolean = false;
  private resetToken: string = '';

  constructor(private authService: AuthService1, private router: Router) {}

  // Step 1: Send OTP
  sendOtp() {
    if (!this.email) {
      alert('Please enter your email.');
      return;
    }

    this.authService.forgotPassword(this.email).subscribe({
      next: (res:string) => {
        console.log(res);
        this.otpSent = true;
        alert('OTP sent to your email.');
      },
      error: (err) => {
        console.error(err);
        alert('Failed to send OTP.');
      }
    });
  }

  // Step 2: Verify OTP
  verifyOtp() {
  if (!this.otp) {
    alert('Please enter the OTP.');
    return;
  }

  this.authService.verifyForgotOtp(this.email, this.otp).subscribe({
    next: (token: string) => {
      console.log('JWT:', token);
      this.resetToken = token; // store the raw JWT
      this.otpVerified = true;
      alert('Email Verified.');
    },
    error: (err) => {
      console.error('OTP verification failed:', err);
      alert('Invalid OTP.');
    }
  });
}

  // Step 3: Save New Password
  saveNewPassword() {
    if (!this.newPassword || !this.confirmPassword) {
      alert('Please fill both password fields.');
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      alert('Passwords do not match.');
      return;
    }

    this.authService.resetPassword(this.resetToken, this.newPassword, this.confirmPassword).subscribe({
      next: (res:string) => {
        console.log(res);
        alert('Password Reset successfully.');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error(err);
        alert('Failed to reset password.');
      }
    });
  }
}
