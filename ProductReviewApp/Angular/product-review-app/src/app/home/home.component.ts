import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  userFirstName: string = localStorage.getItem('firstName') || ''; // Replace 'John' with a default name

  constructor(private router: Router) {}


  logout() {
    // Remove the login status from local storage
    localStorage.removeItem('loginStatus');
    localStorage.removeItem('role');

    // Redirect to the login page or another destination
    this.router.navigate(['/login']);
  }

}
