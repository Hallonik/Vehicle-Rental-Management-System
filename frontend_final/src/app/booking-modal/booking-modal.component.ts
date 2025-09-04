import { Component, Input, Output, EventEmitter,SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Vehicle } from '../customer-dashboard/customer-dashboard.component';
import { AuthService } from '../utils/AuthService';
import { API_URLS } from '../utils/Constants';
import { loadStripe } from '@stripe/stripe-js';
import { Router } from '@angular/router';



@Component({
  selector: 'app-booking-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './booking-modal.component.html',
  styleUrls: ['./booking-modal.component.css'],
})
export class BookingModalComponent {
  @Input() vehicle: Vehicle | null = null;
  @Input() show = false;

  @Output() close = new EventEmitter<void>();
  @Output() pay = new EventEmitter<{ location: string; vehicle: Vehicle }>();

  location = '';
  baseFare = 0;
  rentalDuration: number = 1;
  tax = 0;
  additionalCharges = 0;
  totalFare = 0;
  constructor(private authService: AuthService,private router: Router) {}

  ngOnChanges(changes: SimpleChanges) {
    if (this.vehicle) {
      this.calculateFare();
    }
  }
   onRentalDurationChange() {
    if (this.vehicle) {
      this.calculateFare();
    }
  }

private calculateFare() {// Backend calculation only
const bookingData = {
  vehicleId: this.vehicle!.vehicleId,
  vehicleName: this.vehicle!.name,
  vehicleType: this.vehicle!.type,
  rentalDuration: this.rentalDuration
};

// Call POST API instead of GET (so we can send bookingData in body)
this.authService.postWithToken(API_URLS.CalculateFare, bookingData).subscribe({
  next: (response: any) => {
    if (response?.data) {
      this.baseFare = response.data.baseFare;
      this.tax = response.data.tax;
      this.additionalCharges = response.data.additionalCharges;
this.totalFare = response.data.totalFare;
console.log("Total Fare:", this.totalFare); // "1370.00"

      
    } else {
      console.error('Unexpected response format:', response);
    }
  },
  error: (err) => {
    console.error('Error fetching fare:', err);
  }
});

  }

async payNow() {
  const token = localStorage.getItem('authToken');
  if (!token) {
    console.error('No auth token found');
    return;
  }

  // Decode JWT to extract userName from sub
  const payload = JSON.parse(atob(token.split('.')[1]));
  const userName = payload.sub;

  try {
    // Step 1: Fetch user details using userName
    console.log("UserName:" + userName);
    const userResponse: any = await this.authService
      .getWithoutToken(`http://hallonik.ap-south-1.elasticbeanstalk.com/api/auth/user/${userName}`)
      .toPromise();

    const bookingData = {
      userId: userResponse.userId,
      name: userResponse.fullName,
      vehicleId: this.vehicle?.vehicleId,
      vehicleName: this.vehicle?.name,
      duration: this.rentalDuration,
      location: this.location,
      amount: this.totalFare,
    };

    // Step 2: Create booking (use postWithTokenstring since it's a full URL)
    this.authService
      .postWithTokenstring('http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/create', bookingData)
      .subscribe({
        next: (response: any) => {
          console.log('Booking created:', response);

          const bookingId = response.bookingId;

          // Step 3: Update vehicle status
          const updateBody = {
            vehicle_availability: false,
            vehicle_status: "UNAVAILABLE",
          };
          this.authService
            .patchWithToken(
              `http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/updateStatus/${this.vehicle?.vehicleId}`,
              updateBody
            )
            .subscribe({
              next: (updateResponse: any) => {
                console.log("Vehicle updated:", updateResponse);

                // Step 4: Navigate to payment page
                this.router.navigate(['/payment'], {
                  queryParams: {
                    amount: this.totalFare,
                    vehicleId: this.vehicle?.vehicleId,
                    bookingId: bookingId, 
                  },
                });
              },
              error: (err) => {
                console.error("Error updating vehicle status:", err);
              },
            });
        },
        error: (err) => {
          console.error("Error creating booking:", err);
        },
      });
  } catch (err) {
    console.error("Error fetching user info:", err);
  }
}



  closeModal() {
    this.close.emit();
  }
}
 