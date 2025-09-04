import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../utils/AuthService';
import { RouterModule } from '@angular/router';
import { API_URLS } from '../../utils/Constants';
import html2pdf from 'html2pdf.js';
import { BookingModalComponent } from '../../booking-modal/booking-modal.component'; // 👈 import modal
import { Vehicle } from '../../customer-dashboard/customer-dashboard.component';
import { Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { FormsModule } from '@angular/forms';

interface Booking {
  name:string,
  email:string,
  phoneNumber: string; 
  vehicle_name: string;
  image_Url: string;
  booking_price: number;
  booking_hours: number;
  vehicle_type: string;
  ratings: number;
  ratingCount: number | null;
  booking_status: string;
  refund_status:string;
  booking_date: Date;
  booking_Id:number ;
  payment_Id: number 
  vehicle_Id: number ;


}



@Component({
  selector: 'app-my-booking',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, BookingModalComponent],
  templateUrl: './my-booking.component.html',
  styleUrls: ['./my-booking.component.css']
})
export class MyBookingComponent implements OnInit {
  bookings: Booking[] = [];
  selectedBooking: Booking | null = null;
  today: Date = new Date();

  // 👇 For booking modal
  showBookingModal = false;
  selectedVehicle: Vehicle | null = null;

  hasBookings: boolean = true;   // 👈 flag

  
  // 👇 Add this flag for cancelled invoice
  isCancelledInvoice: boolean = false;

  phoneNumber: string = '';
    constructor(private authService: AuthService, private router: Router) {}


cancelBooking(booking: Booking) {
  if (!booking) return;

  if (!confirm(`Are you sure you want to cancel booking for ${booking.vehicle_name}?`)) {
    return;
  }

  // 1️⃣ Refund payment → 2️⃣ Update vehicle → 3️⃣ Update booking
  this.authService.refundPayment(booking.payment_Id, booking.booking_price).pipe(
    switchMap(() =>
      this.authService.updateVehicleStatus(booking.vehicle_Id, "AVAILABLE", true)
    ),
    switchMap(() =>
      this.authService.updateBookingPaymentStatus(booking.booking_Id, "REFUND")
    )
  ).subscribe({
    next: () => {
      console.log("Booking cancelled successfully.");

      // 4️⃣ Show Cancelled Invoice
      this.selectedBooking = {
        ...booking,
        booking_status: 'REFUND',
        booking_date: new Date(), // system date
      } as Booking;

      this.isCancelledInvoice = true;
      
        this.getUserWiseVehicle();
    },
    error: (err) => {
      console.error("Error during cancellation:", err);
      alert("Failed to cancel booking. Please try again.");
    }
  });
}

// ✅ Rebook Booking
  rebookBooking(booking: any) {
  console.log("Rebook clicked:", booking);
  
  // Map Booking → Vehicle
  this.selectedVehicle = {
    vehicleId: booking.vehicle_id,              // map to backend ID
    name: booking.vehicle_name,                 // booking vehicle name
    type: booking.vehicle_type,                 // type
    price: booking.booking_price,               // booking price
    people: booking.seating_capacity ?? 4,      // fallback if not present
    imageUrl: booking.image_Url ?? '',          // vehicle image
    fuel: booking.fuel_type ?? 'Petrol',        // default if missing
    ratings: booking.ratings ?? '0',            // ratings
    ratingCount: booking.ratingCount ?? 0       // rating count
  };

  // Open modal
  this.showBookingModal = true;
}


  // ✅ Handle modal close
  handleModalClose() {
    this.showBookingModal = false;
    this.selectedVehicle = null;
  }

  // ✅ Handle Pay event from modal
  handlePay(event: { location: string; vehicle: Vehicle }) {
    console.log("Proceeding to payment with:", event);
    this.showBookingModal = false;
  }




// generatePhn(): string {
//   // pick first digit randomly from 7, 8, 9
//   const firstDigit = [7, 8, 9][Math.floor(Math.random() * 3)];
//   // generate remaining 9 digits
//   let remaining = Math.floor(100000000 + Math.random() * 900000000);
//   return `+91-${firstDigit}${remaining}`;
// }



  ngOnInit(): void {
  this.getUserWiseVehicle();
}

getUserWiseVehicle() {
  const userId = localStorage.getItem('userId');   // 👈 from localStorage
  const authToken = localStorage.getItem('authToken');
  const loginToken = localStorage.getItem('loginToken');

  if (!userId || !authToken || !loginToken) {
    console.error("Missing tokens or userId in localStorage");
    this.hasBookings = false;   // 👈 no bookings if tokens missing
    return;
  }

  // 1️⃣ Get successful bookings for user
  this.authService.getWithTokenCustomHeader(
    `http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/user/${userId}/successful`,
    authToken
  ).subscribe({
    next: (res: any) => {
      const bookings = res.bookings || [];

      if (bookings.length === 0) {
        this.hasBookings = false;   // 👈 no bookings found
        this.bookings = [];
        return;
      }

      // 2️⃣ Get user profile
      this.authService.getWithTokenCustomHeader(
        `http://hallonik.ap-south-1.elasticbeanstalk.com/api/auth/profile`,
        loginToken
      ).subscribe({
        next: (profile: any) => {
          // 3️⃣ For each booking → get vehicle details
          const bookingRequests = bookings.map((b: any) =>
            this.authService.getWithTokenCustomHeader(
              `http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/details/${b.vehicleId}`,
              authToken
            )
            .toPromise()
            .then((vehicle: any) => {

              // ✅ Convert booking date to IST
              const bookingDate = new Date(b.bookingDate);
              const bookingDateIST = new Date(
                bookingDate.getTime() + (5.5 * 60 * 60 * 1000)
              );

              const now = new Date();

              let status = "ACTIVE"; // default
              let refundStatus = "ACTIVE";

              // Case 1: booking date is before today → COMPLETED
              if (bookingDateIST < new Date(now.toDateString())) {
                status = "COMPLETED";
                refundStatus = "COMPLETED";
              } else {
                // Case 2: same date → check endTime
                const bookingEnd = new Date(bookingDateIST);
                bookingEnd.setHours(bookingDateIST.getHours() + b.duration);

                if (bookingEnd <= now) {
                  status = "COMPLETED";
                  refundStatus = "COMPLETED";
                }
              }

              return {
                booking_Id: b.bookingId,
                userId: b.userId,
                name: profile.fullName,
                email: profile.email,
                phoneNumber: profile.phone,
                vehicle_name: vehicle.vehicle_name,
                image_Url: vehicle.image_Url?.replace(/'/g, '') || 'assets/logo.png',
                booking_price: b.amount,
                booking_hours: b.duration,
                vehicle_type: vehicle.vehicle_type,
                ratings: vehicle.ratings,
                ratingCount: vehicle.ratingCount,
                booking_status: b.paymentStatus,
                refund_status: refundStatus,   // 👈 ACTIVE / COMPLETED / UPCOMING
                booking_date: bookingDateIST,  // ✅ save IST date
                payment_Id: b.paymentId,
                vehicle_Id: b.vehicleId
              };
            })
          );

          // 4️⃣ Resolve all vehicle requests and set bookings
          Promise.all(bookingRequests).then(finalBookings => {
            this.bookings = finalBookings;
            this.hasBookings = this.bookings.length > 0;   // 👈 update flag
            console.log("Merged bookings:", this.bookings);
          });

        },
        error: (err) => {
          console.error("Error fetching profile:", err);
        }
      });
    },
    error: (err) => {
      console.error("Error fetching successful bookings:", err);
      this.hasBookings = false;   // 👈 API failed → no bookings
    }
  });
}



  // ✅ Open Invoice Popup
  openInvoice(booking: Booking) {
    this.selectedBooking = booking;
  }

  // ✅ Close Invoice Popup
  closeInvoice() {
    this.selectedBooking = null;
  }

  // ✅ Print Invoice
  downloadInvoice() {
    const element = document.getElementById('print-section');
    if (element) {
      const options = {
        margin: 0.5,
        filename: 'invoice.pdf',   // 👈 name of the downloaded PDF
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2 },
        jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
      };

      html2pdf().from(element).set(options).save();
    }
  }
}

