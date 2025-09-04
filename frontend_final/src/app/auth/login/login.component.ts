import { Component, AfterViewInit } from '@angular/core';
import { AuthService } from '../../utils/AuthService';

import { AuthService1 } from '../../utils/AuthService1';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { ForgotPasswordComponent } from '../../forgot-password/forgot-password.component';

@Component({
  selector: 'app-login',  // changed to app-login for clarity
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [FormsModule, NgIf, NgFor, NgSelectModule, HttpClientModule, ForgotPasswordComponent]
})
export class LoginComponent implements AfterViewInit {

  // Sign-up fields
  nameValue = '';
  emailValue = '';
  mobileNumberValue = '';
  passwordValue = '';
  userNameValue='';
  roleValue = 'customer';

  // Sign-in fields
  signInEmail = '';
  signInPassword = '';
  signInRole = 'CUSTOMER';

  // OTP Feature
  otpSent = false;
  otpVerified = false;
  enteredOtp = '';
  registrationSuccess = false;


  loginSuccess = false; // for showing success message
  loginError = false;   // optional, if you want error message

  constructor(private authService1: AuthService1,private authService: AuthService, private router: Router) {}
 

  // ------------------- SIGN-UP -------------------


  // ------------------- SIGN-IN -------------------
  onSignIn(password: string, emailValue: string, role: string,username: string) {
    this.loginSuccess = false;
    this.loginError = false;

    if (!emailValue || !password || !role || !username) {
      this.loginError = true;
      return;
    }   
    

    const payload = {
      email: emailValue.trim(),
      password: password.trim(),
      role: role.trim().toUpperCase()
      
    };

    console.log(payload)

    this.authService1.loginIn(payload).subscribe({
      next: (res: string) => {
        
          const token = res.replace('Login Successful. Token: ', '').trim();
          console.log("the token is "+token)
          this.loginSuccess = true;
          this.authService1.saveToken(token);
          // Optional: decode if you want role/email/time
          // const decoded = this.decodeJwt(res.token);
          // console.log('Decoded token:', decoded);
          setTimeout(() => {
            // this.router.navigate(['/dashboard']);  to navigate to customer or admin dashboard
          }, 1500); // short delay to show message

          this.authService.signIn(username, password).subscribe({
      next: (response: any) => {
        const  roleValue = this.authService.getUserRole()!!.replace(/"/g, '')
        console.log('Sign-in success:', response);
        console.log('Role:', roleValue);
         if (roleValue === 'CUSTOMER') {
          this.router.navigate(['/customer-dashboard']);
        }
        else if (roleValue === 'ADMIN') {
          this.router.navigate(['/admin']);
        }
      },
      error: (err: any) => {
        console.error('Sign-in failed:', err);
      }
    });
        
      },
      error: () => {
        this.loginError = true;
        
      }
    });
  
    
      
  

  }

  decodeJwt(token: string) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }


  // ------------------- OTP HANDLER -------------------
  onOtpButtonClick() {
   if (!this.emailValue || !this.userNameValue) {
    alert('Please enter all required fields (Username & Email) before requesting OTP.');
    return;
  }

    const payload = {
    fullName: this.nameValue.trim(),
    email: this.emailValue.trim(),
    phone: this.mobileNumberValue.trim(),
    password: this.passwordValue.trim(),
    userName: this.userNameValue.trim(),
    role: this.roleValue.toUpperCase()
  };

  console.log(payload)

    if (!this.otpSent) {
      // Send OTP
      this.authService1.sendOtp(payload).subscribe({
        next: (res:string) => {
          console.log('OTP sent:', res);
          this.otpSent = true;
          alert('OTP sent to your email.');
        },
        error: (err) => {
          console.error('Failed to send OTP:', err);
          alert('Failed to send OTP');
        }
      });
    } else {
      // Verify OTP
      this.authService1.verifyOtp(this.emailValue, this.enteredOtp).subscribe({
        next: (res:string) => {
          console.log('OTP verified:', res);
          this.otpVerified = true;
          this.otpSent = false;
          alert('OTP verified successfully.');
        },
        error: (err) => {
          console.error('OTP verification failed:', err);
          alert('Invalid OTP, please try again.');
        }
      });
    }
  }

  // ------------------- FORGOT PASSWORD -------------------
  goToForgotPassword() {
    this.router.navigate(['/forgot-password']);
  }

  // ------------------- ROLE CHANGE HANDLER -------------------
  onRoleChange(selectedValue: string, formType: 'signup' | 'login') {
    const roleUpper = selectedValue.toUpperCase();
    console.log('Selected Role:', selectedValue);

    if (formType === 'signup') {
      this.roleValue = roleUpper;
      this.nameValue = '';
      this.emailValue = '';
      this.mobileNumberValue = '';
      this.userNameValue = '';
      this.passwordValue = '';
      this.otpSent = false;
      this.otpVerified = false;
      this.enteredOtp = '';
      this.registrationSuccess = false;
    }

    if (formType === 'login') {
      this.signInRole = roleUpper;
      this.signInEmail = '';
      this.signInPassword = '';
      this.otpSent = false;
      this.otpVerified = false;
      this.enteredOtp = '';
    }
  }

  onSignUpClick(): void {
  if (!this.otpVerified) {
     this.registrationSuccess = false; 
    
    return;
  }

  
      this.registrationSuccess = true; // This will trigger the HTML message
    
  }


  // ------------------- UI EVENT HANDLER -------------------
  ngAfterViewInit() {
    const container = document.getElementById('container');
    const registerBtn = document.getElementById('register');
    const loginBtn = document.getElementById('login');

    if (registerBtn && loginBtn && container) {
      registerBtn.addEventListener('click', () => container.classList.add("active"));
      loginBtn.addEventListener('click', () => container.classList.remove("active"));
    }
  }
}
