import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Initialize the login status based on local storage
    this.isLoggedInSubject.next(!!localStorage.getItem('loginStatus'));
  }

  login(username: string, password: string): void {
    const body = {
      username: username,
      password: password
    };

    this.http.post('http://localhost:8080/reviews/verifyLogin', body, { responseType: 'text' }).subscribe(
      (response) => {
        if (response === 'Login successful') {
          // Save the login status in local storage
          localStorage.setItem('loginStatus', 'Login Successful');

          // Fetch user details and set in local storage (if needed)

          this.isLoggedInSubject.next(true);
          this.router.navigate(['/home']); // Navigate to the home page or other authorized routes
        } else {
          console.error('Authentication failed:', response);
          // Handle authentication failure here, such as displaying an error message
        }
      },
      (error) => {
        console.error('Error:', error);
        // Handle error
      }
    );
  }

  logout(): void {
    // Clear the login status and user-related data from local storage
    localStorage.removeItem('loginStatus');
    // You can also remove other user-related data from local storage

    this.isLoggedInSubject.next(false);
    this.router.navigate(['/login']); // Navigate to the login page
  }
}
