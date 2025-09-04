// src/app/admin/admin-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';

import { BookingsComponent } from './bookings/bookings.component';
import { VehicleComponent } from './vehicles/vehicles.component';
import { UserManagementComponent } from './users-management/users-management.component';
import { RevenueReportsComponent } from './reports/reports.component';
import { MaintenanceComponent } from './maintenance/maintenance.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { ProfileComponent } from './profile/profile.component';
import { SettingsComponent } from './settings/settings.component';

const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'reports', component: RevenueReportsComponent },
      { path: 'bookings', component: BookingsComponent },
      { path: 'vehicles', component: VehicleComponent },
      { path: 'users', component: UserManagementComponent },
      { path: 'maintenance', component: MaintenanceComponent },
      { path: 'feedback', component: FeedbackComponent },
      { path: 'profile', component: ProfileComponent },
       { path: 'settings', component: SettingsComponent },
      
      // additional admin child routes
      //{ path: 'vehicles', loadChildren: () => import('./../admin/vehicles/vehicles.module').then(m => m) } // optional
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
