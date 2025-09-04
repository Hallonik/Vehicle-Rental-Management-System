import { Component, ElementRef, AfterViewInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { loadStripe, Stripe, StripeElements, StripeCardElement } from '@stripe/stripe-js';
import { AuthService } from '../utils/AuthService';
import { API_URLS } from '../utils/Constants';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-payment',
  standalone: true,
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
  imports: [FormsModule, CommonModule]
})
export class PaymentComponent implements AfterViewInit {
  @ViewChild('cardContainer') cardContainer!: ElementRef;

  private stripe: Stripe | null = null;
  private elements: StripeElements | null = null;
  private card: StripeCardElement | null = null;

  name = '';
  email = '';
  userId = '';
  amount = 0;
  paymentMode = '';
  paymentFor = '';
  remark = '';
  vehicleId = 0;   
  bookingId = 0;   

  cardErrors = '';
  submitting = false;
  success = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    const storedUserId = this.authService.getUserId();
    if (storedUserId) {
      this.userId = storedUserId;
    }
  }

  async ngAfterViewInit() {
    this.route.queryParams.subscribe(async params => {
      this.amount = params['amount'] ? +params['amount'] : 0;
      this.vehicleId = params['vehicleId'] ? +params['vehicleId'] : 0;
      this.bookingId = params['bookingId'] ? +params['bookingId'] : 0;

      if (this.vehicleId) {
        this.fetchVehicleName(this.vehicleId);
      }

      this.stripe = await loadStripe(
        'pk_test_51RwljzLyy5IXJa2tfApE8lFYPNtHvXV6eYMCYNUcFMXeE8OvRX557qkgPjvFbsn5z1NZPeh6cFMOIBkDHjP9WTLY00TTFX8ZSq'
      );
      if (this.stripe) {
        this.elements = this.stripe.elements();
      }
    });
  }

  fetchVehicleName(vehicleId: number): void {
    const token = localStorage.getItem('authToken');
    if (!token || !vehicleId) return;

    this.http.get<any>(`http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/${vehicleId}/type-name`, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (res) => {
        this.paymentFor = res.vehicle_name;
        console.log("Vehicle fetched:", res);
      },
      error: (err) => {
        console.error("Failed to fetch vehicle name ❌", err);
      }
    });
  }

  setPaymentMode(mode: string) {
    this.paymentMode = mode;

    setTimeout(() => {
      if (mode === 'card' && this.cardContainer && !this.card && this.elements) {
        this.card = this.elements.create('card', { hidePostalCode: true });
        this.card.mount(this.cardContainer.nativeElement);

        this.card.on('change', event => {
          this.cardErrors = event.error ? event.error.message : '';
        });
      }
    }, 0);
  }

  get buttonLabel(): string {
    return this.amount > 0 ? `Pay $${this.amount.toFixed(2)}` : 'Pay';
  }

  async submitPayment() {
    this.submitting = true;
    this.cardErrors = '';

    if (!this.amount || this.amount <= 0) {
      this.cardErrors = 'Please enter a valid amount.';
      this.submitting = false;
      return;
    }

    if (this.paymentMode === 'card' && !this.card) {
      this.cardErrors = 'Card input is not ready.';
      this.submitting = false;
      return;
    }

    try {
      const payload = {
        amount: this.amount,
        currency: 'usd',
        name: this.name,
        email: this.email,
        userId: this.userId,
        paymentMode: this.paymentMode,
        paymentFor: this.paymentFor,
        remark: this.remark,
        vehicleId: this.vehicleId,
        bookingId: this.bookingId
      };

      const response: any = await this.authService
        .postWithToken(API_URLS.paymentProcess, payload)
        .toPromise();

      console.log('Payment response', response);

      if (this.paymentMode === 'card' && this.stripe && this.card) {
        const clientSecret = response.clientSecret;
        const result = await this.stripe.confirmCardPayment(clientSecret, {
          payment_method: {
            card: this.card,
            billing_details: { name: this.name, email: this.email }
          }
        });

        if (result.error) {
          this.cardErrors = result.error.message!;
          this.submitting = false;
          return;
        }
      }

      await this.authService.updatePaymentStatus(this.bookingId, 'SUCCESSFUL').toPromise();

      this.authService.getPaymentIdByBookingIdnew(this.bookingId).subscribe({
        next: async(res: any) => {
          const paymentId = res.data;
          console.log("Fetched PaymentId:", paymentId);

          this.authService.setPaymentIdForBooking(this.bookingId, paymentId).subscribe({
            next: (updateRes) => console.log("Booking updated ✅", updateRes),
            error: (err) => console.error("Failed to update booking ❌", err)
          });

          await this.pollPaymentStatus(paymentId);
        },
        error: (err) => console.error("Failed to fetch PaymentId ❌", err)
      });

      this.success = true;
      this.submitting = false;

      this.router.navigate(['/customer-dashboard'], {
        queryParams: { paymentSuccess: true }
      });

    } catch (error: any) {
      console.error('Payment failed:', error);
      this.cardErrors = 'Payment failed. Please try again.';
      this.submitting = false;
    }
  }

  async pollPaymentStatus(paymentId: number) {
  const authToken = localStorage.getItem('authToken');
  if (!authToken) {
    console.error("Missing auth token!");
    return;
  }

  const url = `http://hallonik.ap-south-1.elasticbeanstalk.com/api/paymentprocess/GetPaymentStatus/${paymentId}`;

  const fetchStatus = async () => {
    try {
      const res = await this.authService.getWithTokenCustomHeader(url, authToken).toPromise();
      console.log("Payment status:", res);
      return res;
    } catch (err) {
      console.error("Error fetching payment status:", err);
    }
  };

  // helper delay
  const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

  // Call first time
  await fetchStatus();

  // Wait 2 seconds
  await delay(2000);

  // Call second time
  await fetchStatus();
}


  // ✅ Cancel payment handler
  cancelPayment() {
    const token = localStorage.getItem('authToken');
    if (!token || !this.vehicleId) {
      console.error("Missing auth token or vehicleId ❌");
      return;
    }
    const body={
      
    "vehicle_availability":"true",
    "vehicle_status": "AVAILABLE"

    }

    this.http.patch(
      `http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/updateStatus/${this.vehicleId}`,
      body,
      { headers: { Authorization: `Bearer ${token}` } }
    ).subscribe({
      next: (res: any) => {
        console.log("Vehicle reset ✅", res);
        this.router.navigate(['/customer-dashboard'], {
          queryParams: { paymentSuccess: false }
        });
      },
      error: (err) => {
        console.error("Failed to reset vehicle ❌", err);
        this.router.navigate(['/customer-dashboard'], {
          queryParams: { paymentSuccess: true }
        });
      }
    });
  }
}
