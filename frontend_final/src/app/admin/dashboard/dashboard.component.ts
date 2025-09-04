import { Component, OnInit } from '@angular/core';
import { DashboardService } from './dashboard.service';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { Chart, registerables } from 'chart.js';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  
  stats: any = {};

  // Revenue bar chart
  revenueChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };
  revenueChartOptions: ChartOptions = { responsive: true };

  // Booking overview bar chart
  bookingChartData: ChartConfiguration<'bar'>['data'] = { labels: [], datasets: [] };

  // Vehicle types (donut chart)
  vehicleTypeLabels: string[] = [];
  vehicleTypeCounts: number[] = [];

  addVehicleForm: FormGroup;
  availabilityResult: any = null;

  constructor(private adminSvc: DashboardService, private fb: FormBuilder) {
    this.addVehicleForm = this.fb.group({
      type: ['', Validators.required],
      name: ['', Validators.required],
      total: [1, Validators.required],
      pricePerHour: [0, Validators.required]
    });
  }

  ngOnInit(): void {
    // 👉 Dashboard Summary
    this.adminSvc.getSummaryStats().subscribe({
      next: (data) => this.stats = data,
      error: (err) => console.error('Error fetching dashboard stats', err)
    });

    // 👉 Revenue Summary (Bar Graph)
    this.adminSvc.getRevenueOverMonths().subscribe(res => {
      this.revenueChartData = {
        labels: res.months,
        datasets: [{ data: res.revenue, label: 'Revenue per Month' }]
      };
    });

    // 👉 Booking Overview (Bar Graph)
    this.adminSvc.getBookingOverview().subscribe(r => {
      this.bookingChartData = {
        labels: r.months,
        datasets: [{ data: r.counts, label: 'Bookings per Month' }]
      };
    });

    // 👉 Vehicle Type Distribution (Donut Chart)
    this.adminSvc.getVehicleTypeDistribution().subscribe(r => {
      this.vehicleTypeLabels = r.labels;
      this.vehicleTypeCounts = r.counts;
    });
  }



  
}
