import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-vehicle-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <div class="vehicle-card">
      <!-- Header -->
      <div class="header">
        <h2>{{ data.carName }}</h2>
      </div>

      <!-- Image -->
      <img *ngIf="data.imageUrl" [src]="data.imageUrl" alt="{{ data.carName }}" class="vehicle-image" />

      <!-- Info -->
      <div class="info">
        <p><strong>Price:</strong> {{ data.price | currency:'USD':'symbol':'1.0-0'}} {{ data.priceUnit }}</p>
        <p><strong>Description:</strong> {{ data.description }}</p>
        <p>
          <strong>Rating:</strong> 
          <span class="stars">
            <ng-container *ngFor="let star of stars">
              <span [class.filled]="star <= data.ratings">★</span>
            </ng-container>
          </span>
          ({{ data.ratingCount }} reviews)
        </p>

        <p><strong>Features:</strong></p>
        <ul>
          <li *ngFor="let feature of data.features">
            <span class="feature-icon">{{ feature.icon }}</span>
            {{ feature.text }}
          </li>
        </ul>
      </div>

      <!-- Action Button -->
      <div class="actions">
        <button mat-raised-button color="primary" (click)="close()">Close</button>
      </div>
    </div>
  `,
  styles: [`
    .vehicle-card {
      max-width: 520px;
      max-height: 90vh;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      border-radius: 30px;
      padding: 30px;
      gap: 20px;
      animation: popIn 0.4s ease-out;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      overflow: hidden;

      /* subtle background pattern */
      background-image: radial-gradient(circle, rgba(0,0,0,0.03) 1px, transparent 1px);
      background-size: 25px 25px;
      box-shadow: 0 12px 40px rgba(0,0,0,0.25);
    }

    /* Header */
    .header {
      text-align: center;
      border-bottom: 1px solid rgba(0,0,0,0.1);
      padding-bottom: 15px;
    }
    .header h2 {
      margin: 0;
      font-size: 26px;
      font-weight: 700;
      color: #222;
    }

    /* Image */
    .vehicle-image {
      width: 100%;
      border-radius: 20px;
      object-fit: cover;
      transition: transform 0.35s ease, box-shadow 0.35s ease;
    }
    .vehicle-image:hover {
      transform: scale(1.04);
      box-shadow: 0 12px 24px rgba(0,0,0,0.3);
    }

    /* Info */
    .info p {
      margin: 5px 0;
      line-height: 1.5;
      color: #444;
      font-size: 15px;
    }

    .stars {
      color: #f0c000;
      font-size: 17px;
    }
    .stars .filled {
      color: #fbc02d;
    }

    .info ul {
      list-style: none;
      padding: 0;
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin-top: 6px;
    }
    .info li {
      background: #d9eaff;
      padding: 8px 14px;
      border-radius: 14px;
      font-size: 14px;
      display: flex;
      align-items: center;
      gap: 8px;
      transition: all 0.3s;
    }
    .info li:hover {
      background: #a8d0ff;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }
    .feature-icon {
      font-size: 16px;
    }

    /* Action Button */
    .actions {
      display: flex;
      justify-content: center;
      margin-top: 10px;
    }

    /* Animations */
    @keyframes popIn {
      from { opacity: 0; transform: scale(0.9); }
      to { opacity: 1; transform: scale(1); }
    }

    @keyframes popOut {
      from { opacity: 1; transform: scale(1); }
      to { opacity: 0; transform: scale(0.9); }
    }

    /* Override Material Dialog container */
    ::ng-deep .mat-mdc-dialog-container .mdc-dialog__surface {
      transition: transform 300ms cubic-bezier(0.4, 0, 0.2, 1),
                  opacity 300ms ease-in-out;
      border-radius: 20px;
      background: transparent; /* so our vehicle-card background shows */
      box-shadow: none;
      padding: 0;
    }

    /* Responsive */
    @media (max-width: 600px) {
      .vehicle-card {
        padding: 20px;
      }
      .info li {
        font-size: 12px;
        padding: 6px 10px;
      }
    }
  `]
})
export class VehicleDetailsDialogComponent {
  stars = [1, 2, 3, 4, 5];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<VehicleDetailsDialogComponent>
  ) {}

  close() {
    // Add a little exit animation before closing
    const dialogElement = document.querySelector('.vehicle-card') as HTMLElement;
    if (dialogElement) {
      dialogElement.style.animation = 'popOut 0.3s ease forwards';
      setTimeout(() => this.dialogRef.close(), 280); // wait until animation finishes
    } else {
      this.dialogRef.close();
    }
  }
}
