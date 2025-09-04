import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../utils/AuthService';

@Component({
  selector: 'app-layout',
  standalone: false,
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent {

  constructor(private router: Router,private authService: AuthService){}
logout() {

  this.authService.clearStorage();

  // navigate to home/login page
  this.router.navigate(['/']);
}

}
