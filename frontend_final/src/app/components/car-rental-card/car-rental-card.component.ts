import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { VehicleDetailsDialogComponent } from './vehicle-details-dialog/vehicle-details-dialog.component';

@Component({
  selector: 'app-car-rental-card',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './car-rental-card.component.html',
  styleUrls: ['./car-rental-card.component.css'],
})
export class CarRentalCardComponent {
  @Input() carName = '';
  @Input() price = 0;
  @Input() priceUnit = '/ day';
  @Input() description = '';
  @Input() features: { icon: string; text: string | number }[] = [];
  @Input() rating = '';
  @Input() ratingCount = 0;
  @Input() badgeText = '';
  @Input() imageUrl?: string;
  @Input() ratings ='' 

   // Output event to send "Book" action to parent
  @Output() book = new EventEmitter<void>();

    constructor(private dialog: MatDialog) {}

  onBookClick() {
    this.book.emit();
  }

  openDetails() {
    this.dialog.open(VehicleDetailsDialogComponent, {
      width: '500px',
      data: {
        carName: this.carName,
        price: this.price,
        priceUnit: this.priceUnit,
        description: this.description,
        features: this.features,
        rating: this.rating,
        ratingCount: this.ratingCount,
        badgeText: this.badgeText,
        imageUrl: this.imageUrl,
        ratings: this.ratings
      }
    });
  }

  
}