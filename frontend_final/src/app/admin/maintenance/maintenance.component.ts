import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MaintenanceService } from './maintenance.service';
import { Maintenance } from './maintenance.model';
import { MatSnackBar } from '@angular/material/snack-bar'; 

@Component({
  selector: 'app-maintenance',
  templateUrl: './maintenance.component.html',
  styleUrls: ['./maintenance.component.scss']
})
export class MaintenanceComponent implements OnInit {
  displayedColumns = ['maintenanceId','vehicleType','vehicleName','serviceDate','cost','description','actions'];
  dataSource = new MatTableDataSource<Maintenance>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<any>; 

  constructor(
    private fb: FormBuilder, 
    private svc: MaintenanceService,
    private snack: MatSnackBar
  ) {}

  form = this.fb.group({
    vehicleId: ['', Validators.required],
    vehicleType: [{ value: '', disabled: true }, Validators.required],
    vehicleName: [{ value: '', disabled: true }, Validators.required],
    serviceDate: [null as Date | null, Validators.required],
    cost: [null as number | null, [Validators.required, Validators.min(0)]],
    description: ['']
  });

  ngOnInit(): void {
    this.loadMaintenances();
  }

  loadMaintenances(): void {
    this.svc.getAll().subscribe(list => {
      this.dataSource.data = list;
      if (this.paginator) this.dataSource.paginator = this.paginator;
      if (this.sort) this.dataSource.sort = this.sort;
    });
  }

  fetchVehicleDetails(): void {
    const vehicleId = this.form.controls['vehicleId'].value;
    if (!vehicleId) {
      this.snack.open('Please enter a Vehicle ID', 'OK', { duration: 2000 });
      return;
    }

    this.svc.getVehicleTypeAndName(vehicleId).subscribe({
      next: (res) => {
        this.form.patchValue({
          vehicleType: res.vehicle_type,
          vehicleName: res.vehicle_name
        });
        this.snack.open('Vehicle details loaded successfully', 'OK', { duration: 1500 });
      },
      error: () => {
        this.form.patchValue({ vehicleType: '', vehicleName: '' });
        this.snack.open('Vehicle ID not found. Please try again.', 'OK', { duration: 2000 });
      }
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = this.form.getRawValue();
    const vehicleId = Number(payload.vehicleId);
    this.svc.add(payload).subscribe({
      next: (savedRecord) => {
        // Update table instantly
        this.dataSource.data = [...this.dataSource.data, savedRecord];
        this.table.renderRows();

        // Set vehicle status to UNAVAILABLE
        this.svc.updateVehicleStatus(vehicleId, 'UNAVAILABLE', false).subscribe({
          next: () => {
            this.form.reset();
            this.snack.open('Maintenance added & vehicle set to UNAVAILABLE', 'OK', { duration: 2000 });
          },
          error: () => {
            this.snack.open('Maintenance added but failed to update vehicle status', 'OK', { duration: 2000 });
          }
        });
      },
      error: () => {
        this.snack.open('Failed to add maintenance record', 'OK', { duration: 2000 });
      }
    });
  }

  delete(id: number): void {
    if (confirm('Delete this maintenance record?')) {
      const record = this.dataSource.data.find(m => m.maintenanceId === id);
      if (!record) {
        this.snack.open('Record not found', 'OK', { duration: 2000 });
        return;
      }
      const vehicleId = Number(record.vehicleId);

      this.svc.remove(id).subscribe({
        next: () => {
          // Update table instantly
          this.dataSource.data = this.dataSource.data.filter(m => m.maintenanceId !== id);
          this.table.renderRows();

          // Set vehicle status to AVAILABLE
          this.svc.updateVehicleStatus(vehicleId, 'AVAILABLE', true).subscribe({
            next: () => {
              this.snack.open('Maintenance deleted & vehicle set to AVAILABLE', 'OK', { duration: 2000 });
            },
            error: () => {
              this.snack.open('Maintenance deleted but failed to update vehicle status', 'OK', { duration: 2000 });
            }
          });
        },
        error: () => {
          this.snack.open('Failed to delete maintenance record', 'OK', { duration: 2000 });
        }
      });
    }
  }

  get totalCost(): number {
    return this.dataSource.filteredData.reduce((sum, m) => sum + (m.cost || 0), 0);
  }
}
