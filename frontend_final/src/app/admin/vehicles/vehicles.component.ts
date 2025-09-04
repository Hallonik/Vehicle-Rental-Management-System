import { Component, OnInit } from '@angular/core';
import { VehicleService, Vehicle } from './vehicle.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-vehicles',
  standalone: false,
  templateUrl: './vehicles.component.html',
  styleUrls: ['./vehicles.component.scss']
})
export class VehicleComponent implements OnInit {
  vehicles: Vehicle[] = [];
  addVehicleForm: FormGroup;
  isAdding = false;

  displayedColumns: string[] = [
    'vehicle_id',
    'vehicle_type',
    'vehicle_name',
    'image_Url',
    'vehicle_status',
    'vehicle_rate_per_hour',
    'vehicle_hourly_rate_late_fee',
    'vehicle_availability',
    'seating_capacity',
    'fuel_type',
    'ratings',
    'ratingCount',
    'total',
    'actions'
  ];

  constructor(
    private vehicleSvc: VehicleService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {
    this.addVehicleForm = this.fb.group({
      vehicle_type: ['', Validators.required],
      vehicle_name: ['', Validators.required],
      image_Url: ['', Validators.required],
      vehicle_rate_per_hour: [0, [Validators.required, Validators.min(0)]],
      vehicle_hourly_rate_late_fee: [0, [Validators.required, Validators.min(0)]],
      seating_capacity: [1, [Validators.required, Validators.min(1)]],
      fuel_type: ['', Validators.required],
      ratings: [0],
      ratingCount: [0]
    });
  }

  ngOnInit(): void {
    this.loadVehicles();
  }

  loadVehicles() {
    this.vehicleSvc.getVehicles().subscribe(data => {
      this.vehicles = data;
    });
  }

  toggleAddForm() {
    this.isAdding = !this.isAdding;
  }

  submitAddVehicle() {
    if (this.addVehicleForm.valid) {
      this.vehicleSvc.addVehicle(this.addVehicleForm.value).subscribe({
        next: (newVehicle) => {
          this.snack.open('Vehicle added successfully!', 'OK', { duration: 1800 });
          this.addVehicleForm.reset({ ratings: 0, ratingCount: 0 });
          this.isAdding = false;
          this.loadVehicles(); // reload to update total
        },
        error: () => {
          this.snack.open('Failed to add vehicle', 'OK', { duration: 2000 });
        }
      });
    }
  }

  removeVehicle(row: Vehicle) {
    if (!row.vehicle_id) return;

    if (confirm(`Delete vehicle "${row.vehicle_name}"? This cannot be undone.`)) {
      this.vehicleSvc.deleteVehicle(row.vehicle_id).subscribe({
        next: () => {
          this.vehicles = this.vehicles.filter(v => v.vehicle_id !== row.vehicle_id);
          this.snack.open('Vehicle deleted', 'OK', { duration: 1800 });
        },
        error: () => {
          this.snack.open('Failed to delete vehicle', 'OK', { duration: 2000 });
        }
      });
    }
  }
}
