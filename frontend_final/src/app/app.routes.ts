import { Routes } from '@angular/router';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { LoginComponent } from './auth/login/login.component';
import { CustomerDashboardComponent } from './customer-dashboard/customer-dashboard.component';
import { PaymentComponent } from './payment/payment.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },

  
  { path: 'customer-dashboard', component: CustomerDashboardComponent },
   {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.module').then(m => m.AdminModule),
  },
  {path: 'payment', component: PaymentComponent},
  { path: 'forgot-password', loadComponent: () => import('./forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) }
];
