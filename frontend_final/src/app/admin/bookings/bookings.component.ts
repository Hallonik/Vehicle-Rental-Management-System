import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Booking } from './booking.model';
import { ChartConfiguration } from 'chart.js';
import { BookingService } from './booking.service';

@Component({
  selector: 'app-bookings',
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.scss']
})
export class BookingsComponent implements OnInit {
  filterForm: FormGroup;
  displayedColumns = [
    'bookingId','userId','name','vehicleId','vehicleName','duration',
    'bookingDate','location','amount','paymentStatus','actions'
  ];
  dataSource = new MatTableDataSource<Booking>([]);
  bookings: Booking[] = [];
  loading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private fb: FormBuilder,
    private snack: MatSnackBar,
    private bookingService: BookingService
  ) {
    this.filterForm = this.fb.group({
      query: [''],
      status: ['']
    });
  }

  ngOnInit(): void {
    this.loadMonthlyBookings();
    this.loadBookings();
    this.loadStatusCount(); 
    this.filterForm.valueChanges.subscribe(() => this.applyFilters());
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  /** ✅ Load all bookings */
  loadBookings() {
    this.bookingService.getBookings().subscribe({
      next: (data) => {
        this.bookings = data;
        this.dataSource.data = this.bookings;
        this.dataSource.paginator = this.paginator; 
        this.dataSource.sort = this.sort;
        this.updateDonutChart();
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.snack.open('Failed to load bookings', 'OK', { duration: 2000 });
      }
    });
  }

  /** ✅ Load monthly bookings for bar chart */
  loadMonthlyBookings() {
    this.bookingService.getMonthlyBookings().subscribe({
      next: (data) => {
        const labels = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

        this.barChartData = {
          labels,
          datasets: [
            {
              label: 'Bookings',
              data: labels.map((_, i) => data[(i+1).toString()] ?? 0),
              backgroundColor: '#42A5F5'
            }
          ]
        };
      },
      error: (err) => {
        console.error(err);
        this.snack.open('Failed to load monthly data', 'OK', { duration: 2000 });
      }
    });
  }

  /** ✅ Fetch status counts for donut chart */
  loadStatusCount() {
    this.bookingService.getPaymentStatusCount().subscribe({
      next: (data) => {
        this.donutChartData = {
          labels: ['SUCCESSFUL', 'UNSUCCESSFUL'],
          datasets: [
            {
              data: [data['SUCCESSFUL'] ?? 0, data['UNSUCCESSFUL'] ?? 0],
              backgroundColor: ['#4CAF50', '#F44336']
            }
          ]
        };
      },
      error: (err) => {
        console.error(err);
        this.snack.open('Failed to load payment status counts', 'OK', { duration: 2000 });
      }
    });
  }

  /** ✅ Apply filters */
  applyFilters() {
    const { query, status } = this.filterForm.value;
    let filtered = [...this.bookings];

    if (query) {
      const q = query.toLowerCase();
      filtered = filtered.filter(b => {
        const bookingDateOnly = b.bookingDate ? new Date(b.bookingDate).toISOString().slice(0, 10) : '';
        return (
          b.bookingId.toString().includes(q) ||
          b.userId?.toString().includes(q) ||
          b.name?.toLowerCase().includes(q) ||
          b.vehicleId?.toString().includes(q) ||
          b.vehicleName?.toLowerCase().includes(q) ||
          b.duration?.toString().includes(q) ||
          bookingDateOnly.includes(q) ||
          b.location?.toLowerCase().includes(q) ||
          b.amount?.toString().includes(q) ||
          b.paymentStatus?.toLowerCase().includes(q)
        );
      });
    }

    if (status) {
      filtered = filtered.filter(b => b.paymentStatus === status);
    }

    this.dataSource.data = filtered;
    this.dataSource.paginator = this.paginator;  // re-hook paginator
    this.dataSource.sort = this.sort;
  }

  /** ✅ Reset filters */
  resetFilters() {
    this.filterForm.reset({ query: '', status: '' });
    this.dataSource.data = this.bookings;
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  /** ✅ Delete booking */
remove(bookingId: number, vehicleId?: number) {
  if (confirm('Are you sure you want to delete this booking?')) {
    if (vehicleId) {
      // ✅ Step 1: Update vehicle status before deleting booking
      this.bookingService.updateVehicleStatus(vehicleId).subscribe({
        next: () => {
          this.snack.open('Vehicle status updated to AVAILABLE', 'OK', { duration: 2000 });

         

          // ✅ Step 2: Now delete booking
          this.bookingService.deleteBooking(bookingId).subscribe({
            next: (res) => {
              if (res.success) {
                this.bookings = this.bookings.filter(b => b.bookingId !== res.bookingId);
                this.dataSource.data = this.bookings;
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;

                this.snack.open(res.message, 'OK', { duration: 2000 });

                  this.loadBookings();
                this.loadStatusCount();
                this.loadMonthlyBookings();
              }
            },
            error: (err) => {
              console.error(err);
              this.snack.open('Failed to remove booking', 'OK', { duration: 2000 });
            }
          });
        },
        error: (err) => {
          console.error(err);
          this.snack.open('Vehicle not present or failed to update.', 'OK', { duration: 2000 });
        }
      });
    } else {
      // 🚨 If no vehicleId provided, just delete booking
      this.bookingService.deleteBooking(bookingId).subscribe({
        next: (res) => {
          if (res.success) {
            this.bookings = this.bookings.filter(b => b.bookingId !== res.bookingId);
            this.dataSource.data = this.bookings;
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;

            this.snack.open(res.message, 'OK', { duration: 2000 });
              this.loadBookings();
                this.loadStatusCount();
                this.loadMonthlyBookings();
          }
        },
        error: (err) => {
          console.error(err);
          this.snack.open('Failed to remove booking', 'OK', { duration: 2000 });
        }
      });
    }
  }
}




  /** ✅ Export CSV */
  exportCsv() {
    const rows = this.dataSource.data;
    const header = ['Booking ID','User ID','Name','Vehicle ID','Vehicle Name','Duration','Date','Location','Amount','Status'];
    const body = rows.map(r => [
      r.bookingId, r.userId, r.name, r.vehicleId, r.vehicleName,
      r.duration, r.bookingDate, r.location, r.amount, r.paymentStatus
    ]);
    const csv = [header, ...body].map(x => x.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `bookings-${new Date().toISOString().slice(0,10)}.csv`;
    a.click();
    URL.revokeObjectURL(a.href);
  }

  // --- Charts ---
  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ label: 'Bookings', data: [], backgroundColor: '#42A5F5' }]
  };

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: {
      x: { title: { display: true, text: 'Month' } },
      y: { title: { display: true, text: 'No. of Bookings' }, beginAtZero: true }
    }
  };

  donutChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['SUCCESSFUL', 'UNSUCCESSFUL'],
    datasets: [{
      data: [0, 0],
      backgroundColor: ['#4CAF50', '#F44336']
    }]
  };

  donutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } }
  };

  private updateDonutChart() {
    const success = this.bookings.filter(b => b.paymentStatus === 'SUCCESSFUL').length;
    const fail = this.bookings.filter(b => b.paymentStatus === 'UNSUCCESSFUL').length;
    this.donutChartData.datasets[0].data = [success, fail];
  }
}
