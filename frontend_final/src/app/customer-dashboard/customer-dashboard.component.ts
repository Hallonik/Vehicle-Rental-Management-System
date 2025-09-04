import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CarRentalCardComponent } from '../components/car-rental-card/car-rental-card.component';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { AuthService } from '../utils/AuthService';
import { API_URLS } from '../utils/Constants';
import { ActivatedRoute, Route, Router, RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MyBookingComponent } from '../auth/my-booking/my-booking.component';
import { AdminModule } from '../admin/admin.module';
import { forkJoin } from 'rxjs';





export interface Vehicle {
  vehicleId: number;
  name: string;
  type: string;
  price: number;
  people?: number;
  imageUrl?: string;
  fuel: string;
  ratings?: string;
  ratingCount?: number;
}

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, AdminModule,  FormsModule,  CarRentalCardComponent,BookingModalComponent,MatSnackBarModule,MyBookingComponent,RouterModule ],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.css']
})
export class CustomerDashboardComponent implements OnInit {

  paymentSuccess = false
  vehicles: Vehicle[] = [];
  filteredVehicles: Vehicle[] = [];
  activeMenu: string = 'Home';
  searchTerm: string = '';
  showBooking: boolean = false;
  selectedVehicle: Vehicle | null = null;
  openBookingModal(vehicle: Vehicle) {
    console.log("Book Pressed")
    this.selectedVehicle = vehicle;
    this.showBooking = true;
  }

  closeBooking() {
    this.showBooking = false;
    this.selectedVehicle = null;
  }
  handlePayment(event: any) {
  console.log('Payment event received:', event);
}


bookyourride(){

 this.router.navigate(['/customer-dashboard']);


}
  filters = {
    type: '',
    priceMax: 5000,
    fuel :''

  };

  vehicleTypes = ['Hatchback','Sedan','SUV','MUV','Bike','Scooter','Electric Car','Electric Scooter','Luxury Car','Pickup Truck','Van'
];
  fuelTypes : string []=['Petrol','Diesel','Electric'];



  constructor(private authService: AuthService,private route: ActivatedRoute,private snackBar: MatSnackBar,private router: Router) {}

ngOnInit(): void {
  this.route.queryParams.subscribe(params => {
    const amountParam = params['paymentSuccess'];
    console.log('Payment param:', amountParam);

    if (amountParam === 'true') {
      this.paymentSuccess = true;
      this.snackBar.open('Payment Successful! ✅', 'Close', {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['success-snackbar']
      });

      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { paymentSuccess: null },
        queryParamsHandling: 'merge',
      });

    } else if (amountParam === 'false') {
      this.paymentSuccess = false;
      this.snackBar.open('Payment Incomplete! ❌', 'Close', {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });

      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { paymentSuccess: null },
        queryParamsHandling: 'merge',
      });
    }
  });

  // ✅ Step 1: Reset vehicles before loading
  this.resetVehiclesBeforeNow();
}

resetVehiclesBeforeNow(): void {
  const token = localStorage.getItem('authToken');
  if (!token) {
    console.error("No authToken found in local storage");
    this.loadVehiclesFromAPI(); // fallback to loading
    return;
  }

  console.log("Resetting vehicles before now...");

  this.authService.getWithTokenNew("http://hallonik.ap-south-1.elasticbeanstalk.com/api/bookings/successful/beforeNow/vehicleIds")
    .subscribe({
      next: (response: any) => {
        if (response.success && Array.isArray(response.vehicleIds) && response.vehicleIds.length > 0) {
          console.log("Vehicle IDs to reset:", response.vehicleIds);

          // Build array of PATCH observables
          const updateRequests = response.vehicleIds.map((vehicleId: number) => {
            const body = {
              vehicle_availability: "true",
              vehicle_status: "AVAILABLE"
            };
            return this.authService.patchWithTokenNew(
              `http://hallonik.ap-south-1.elasticbeanstalk.com/api/vehicles/updateStatus/${vehicleId}`,
              body
            );
          });

          // Run all updates in parallel and wait for completion
          forkJoin(updateRequests).subscribe({
            next: (results) => {
              console.log("All vehicles updated successfully:", results);
              this.loadVehiclesFromAPI(); // ✅ only after updates complete
            },
            error: (err) => {
              console.error("Error updating some vehicles:", err);
              this.loadVehiclesFromAPI(); // still reload
            }
          });

        } else {
          console.log("No vehicles to reset.");
          this.loadVehiclesFromAPI(); // fallback
        }
      },
      error: (err) => {
        console.error("Failed to fetch vehicleIds:", err);
        this.loadVehiclesFromAPI(); // fallback
      }
    });
}





 loadVehiclesFromAPI(): void {
  this.authService.getWithToken(API_URLS.getVehicle).subscribe({
    next: (response: any) => {
      if (!Array.isArray(response.data)) {
        console.error('Unexpected response format:', response);
        this.vehicles = [];
        this.filteredVehicles = [];
        return;
      }

      // Map backend VehicleInfo to frontend Vehicle
      this.vehicles = response.data.map((v: any) => ({
        vehicleId: v.vehicle_id,
        name: v.vehicle_name,
        type: v.vehicle_type,
        price: v.vehicle_rate_per_hour,
        people: v.seating_capacity,
        imageUrl: v.image_Url?.replace(/'/g, '') || '',// add backend image URL here if available
        fuel: v.fuel_type,
        ratings: v.ratings ?? 0,
        ratingCount: v.ratingCount ?? 0
      }));

      this.filteredVehicles = [...this.vehicles];
      this.applyFilters();
    },
    error: (err) => {
      console.error('Error fetching vehicles:', err);
      this.vehicles = [];
      this.filteredVehicles = [];
    }
  });
}
setActiveMenu(menu: string) {
  this.activeMenu = menu;

  switch(menu) {
    case 'Home':
      this.applyFilters();
      break;
    case 'details':
      this.applyFilters();
      break;
    case 'profile':
      // Profile page does not need filters, just render it
      break;
    default:
      break;
  }
}


logout() {
  this.authService.clearStorage();
  console.log('Customer Logout pressed  ')
  this.router.navigate(['/']);
}


  applyFilters() {
    const term = this.searchTerm.toLowerCase();
    this.filteredVehicles = this.vehicles.filter(v => {
      const matchesType = this.filters.type ? v.type === this.filters.type : true;
      const matchesFuel = this.filters.fuel ? v.fuel === this.filters.fuel : true;
      const matchesPrice = v.price <= this.filters.priceMax;
      
      const matchesSearch = !term || (v.name.toLowerCase().includes(term)) || (v.type.toLowerCase().includes(term) || v.fuel?.includes(term) );
      return matchesType && matchesPrice && matchesFuel && matchesSearch;
    });
  }

  clearFilters() {
    this.filters = { type: '', priceMax: 5000, fuel: '' };
    this.searchTerm = '';
    this.applyFilters();
  }

  onSearch() {
    this.applyFilters();
  }

  // toggleWishlist(vehicle: Vehicle) {
  //   vehicle.isWishlist = !vehicle.isWishlist;
  // }

  rentVehicle(vehicle: Vehicle) {
    alert(`Renting vehicle: ${vehicle.name}`);
  }

  feedback = {
  name: '',
  email: '',
  rating: 0,
  message: ''
};

feedbackSubmitted = false;

setRating(star: number) {
  this.feedback.rating = star;
}

submitFeedback() {
  console.log("Submitting feedback:", this.feedback);

  this.authService.postWithToken(API_URLS.submitFeedback, this.feedback).subscribe({
    next: (response: any) => {
      console.log("Feedback API response:", response);
      this.feedbackSubmitted = true;

      // ✅ Show success message
      this.snackBar.open('Thank you for your feedback! 🎉', 'Close', {
        duration: 4000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['success-snackbar']
      });

      // Reset form after submission
      setTimeout(() => {
        this.feedback = { name: '', email: '', rating: 0, message: '' };
        this.feedbackSubmitted = false;
      }, 3000);
    },
    error: (err) => {
      console.error("Error submitting feedback:", err);
      this.snackBar.open('Failed to submit feedback ❌', 'Close', {
        duration: 4000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });
    }
  });
}

/*
submitFeedback() {
  console.log("Feedback submitted:", this.feedback);
  this.feedbackSubmitted = true;

  // Reset after submission (optional)
  setTimeout(() => {
    this.feedback = { name: '', email: '', rating: 0, message: '' };
    this.feedbackSubmitted = false;
  }, 3000);
}*/

}
