import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true,
  imports:[FormsModule,BrowserModule]
})
export class NavbarComponent {
  activeMenu: string = 'Home';
  searchTerm: string = '';

  @Output() menuChange = new EventEmitter<string>();
  @Output() search = new EventEmitter<string>();

  setActiveMenu(menu: string) {
    this.activeMenu = menu;
    this.menuChange.emit(menu); // send to parent
  }

  onSearch() {
    this.search.emit(this.searchTerm); // send to parent
  }
}
