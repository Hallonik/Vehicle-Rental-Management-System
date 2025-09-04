import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
//import { routes } from './app.routes'; // or wherever you defined the routes
import {  Routes } from '@angular/router';
import { CustomerDashboardComponent } from './customer-dashboard/customer-dashboard.component';
import { HttpClientModule } from '@angular/common/http';


const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'customer-dashboard', component: CustomerDashboardComponent }

];
@NgModule({
  imports: [RouterModule.forRoot(routes), HttpClientModule],
  exports: [RouterModule]
})
export class AppRoutingModule {}
