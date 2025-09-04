import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ChartData, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss'],
})
export class RevenueReportsComponent implements OnInit {
  // Payments table
  payments: any[] = [];
  paymentColumns = [
    'paymentId',
    'name',
    'email',
    'amount',
    'paymentMode',
    'paymentFor',
    'status',
    'remark',
    'actions'
  ];

  // Donut chart
  donutChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  donutChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    plugins: { legend: { position: 'bottom' } },
  };

  loading = false;
  noDataMessage: string = ''; 

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadPayments();
    this.loadPaymentModeCounts();
  }

  // ✅ Fetch all payments
  loadPayments(): void {
    const token = localStorage.getItem('authToken');
    this.loading = true;
    this.http
      .get<any>('http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess/AllPayments', {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` }),
      })
      .subscribe({
        next: (res) => {
          this.payments = res.data || [];
          if (!this.payments.length) {
            alert(res.message || 'No payments found');
          }
          this.loading = false;
        },
        error: () => {
          alert('Error fetching payments');
          this.loading = false;
        },
      });
  }

  // ✅ Delete payment by ID
  deletePayment(paymentId: number): void {
    const token = localStorage.getItem('authToken');
    if (!confirm('Are you sure you want to delete this payment?')) return;

    this.http
      .delete<any>(`http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess/delete/${paymentId}`, {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` }),
      })
      .subscribe({
        next: (res) => {
          alert(res.message);
          this.loadPayments(); // refresh table
          this.loadPaymentModeCounts(); // refresh chart
        },
        error: () => {
          alert('Error deleting payment');
        },
      });
  }

  // ✅ Get count by payment mode
  loadPaymentModeCounts(): void {
    const token = localStorage.getItem('authToken');
    this.http
      .get<any>('http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess/countByMode', {
        headers: new HttpHeaders({ Authorization: `Bearer ${token}` }),
      })
      .subscribe({
        next: (res) => {
          const data = res.data || {};
          if (Object.keys(data).length === 0) {
            this.donutChartData = { labels: [], datasets: [] };
            return;
          }
          this.donutChartData = {
            labels: Object.keys(data),
            datasets: [
              {
                data: Object.values(data) as number[],
                backgroundColor: ['#42a5f5', '#66bb6a', '#ffca28', '#ef5350'],
              },
            ],
          };
        },
        error: () => {
          alert('Error loading payment mode counts');
        },
      });
  }

  // ✅ Export payments table to CSV
  exportToCsv(): void {
    if (!this.payments.length) {
      alert('No payments to export');
      return;
    }
    const headers = Object.keys(this.payments[0]).join(',');
    const rows = this.payments.map((p) => Object.values(p).join(','));
    const csvContent = [headers, ...rows].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'payments.csv';
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
