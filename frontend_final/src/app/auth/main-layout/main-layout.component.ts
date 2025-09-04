import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { LoginComponent } from '../login/login.component';
import { BrowserModule } from '@angular/platform-browser';

@Component({
  selector: 'app-main-layout',
  standalone: true,
 imports: [RouterModule,NavbarComponent,LoginComponent,BrowserModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {

}
