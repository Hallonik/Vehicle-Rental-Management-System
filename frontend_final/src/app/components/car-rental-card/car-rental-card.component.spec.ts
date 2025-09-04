import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarRentalCardComponent } from './car-rental-card.component';

describe('CarRentalCardComponent', () => {
  let component: CarRentalCardComponent;
  let fixture: ComponentFixture<CarRentalCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarRentalCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CarRentalCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
