// src/app/admin/admin.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import {  RevenueReportsComponent } from './reports/reports.component';
import { RouterModule } from '@angular/router';
import { AdminRoutingModule } from './admin-routing.module';
import { MaterialModule } from '../material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { MatCardModule } from '@angular/material/card';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
// Angular Material
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';

import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';

// Angular Material (add to project if not yet installed)

import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';



import { BookingsComponent } from './bookings/bookings.component';
import { VehicleComponent } from './vehicles/vehicles.component';

import { UserManagementComponent } from './users-management/users-management.component';
import { UserDialogComponent } from './users-management/user-dialog.component';
import { MatChipsModule } from '@angular/material/chips';
import { MaintenanceComponent } from './maintenance/maintenance.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { ProfileComponent } from './profile/profile.component';
import { SettingsComponent } from './settings/settings.component';








@NgModule({
  declarations: [
    LayoutComponent,
    DashboardComponent,
    RevenueReportsComponent,
     BookingsComponent ,
     VehicleComponent,
     UserManagementComponent,
     UserDialogComponent,
     MaintenanceComponent,
     FeedbackComponent,
     ProfileComponent,
     SettingsComponent,
     
  ],
  imports: [
    CommonModule,
    RouterModule,
    AdminRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    NgChartsModule,
    MatCardModule,
      MatChipsModule,
      MatSnackBarModule,


      MatPaginatorModule ,
      MatSortModule ,
      MatTooltipModule ,
      MatMenuModule ,



    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatProgressSpinnerModule,

    // Charts
    NgChartsModule,
    MatDividerModule,

    // Routing
   
  ],
  exports: [
    ProfileComponent   // ✅ now reusable outside AdminModule
  ]
})
export class AdminModule {}
