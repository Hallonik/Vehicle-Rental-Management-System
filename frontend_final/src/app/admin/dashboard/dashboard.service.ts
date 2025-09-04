import { Injectable } from '@angular/core';
import { Observable, forkJoin, map } from 'rxjs';
import { AuthService } from '../../utils/AuthService';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private authservice: AuthService) {}

  // 👉 Combines stats from multiple endpoints
  getSummaryStats(): Observable<any> {
    const stats$ = this.authservice.getWithTokenNew('http://hallonik.ap-south-1.elasticbeanstalk.com/api/admin/Get_admin_dash_summary');
    const activeBookings$ = this.authservice.getWithTokenNew('http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/statusCount');
    const revenueThisMonth$ = this.authservice.getWithTokenNew('http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/successful/amount/currentMonth');

    return forkJoin([stats$, activeBookings$, revenueThisMonth$]).pipe(
      map(([stats, bookingStatus, revenue]) => ({
        totalVehicles: stats.totalVehicles || 0,
        availableVehicles: stats.availableVehicles || 0,
        totalUsers: stats.totalUsers || 0,
        activeBookings: bookingStatus.SUCCESSFUL || 0,
        totalRevenue: revenue.totalSuccessfulAmount || 0
      }))
    );
  }

  // 👉 Revenue Summary - yearly revenue bar graph
  getRevenueOverMonths(): Observable<{ months: string[], revenue: number[] }> {
    return this.authservice.getWithTokenNew('http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/successful/amount/yearly')
      .pipe(map(res => {
        const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        const revenue = months.map((_, i) => res[i+1] || 0);
        return { months, revenue };
      }));
  }

  // 👉 Booking Overview - monthly booking counts
  getBookingOverview(): Observable<{ months: string[], counts: number[] }> {
    return this.authservice.getWithTokenNew('http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/monthly')
      .pipe(map(res => {
        const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        const counts = months.map((_, i) => res[i+1] || 0);
        return { months, counts };
      }));
  }

  // 👉 Vehicle Type Distribution - donut chart
  getVehicleTypeDistribution(): Observable<{ labels: string[], counts: number[] }> {
    return this.authservice.getWithTokenNew('http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/typecount')
      .pipe(map(res => {
        const labels = Object.keys(res);
        const counts = Object.values(res).map(v => Number(v));
        return { labels, counts };
      }));
  }

  

  
}
