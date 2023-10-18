import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = "";
  password: string = "";

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    const body = {
      username: this.username,
      password: this.password
    };

    this.http.post('http://localhost:8080/reviews/verifyLogin', body, { responseType: 'text' }).subscribe(
      (response) => {
        if (response === 'Login successful') {
          console.log('Login successful', response); // Log the success response
          this.router.navigate(['/review-list']); // Redirect to the reviews list page
        } else {
          console.error('Authentication failed:', response);
          // Handle authentication failure here, such as displaying an error message
        }
      },
      (error) => {
        console.error('Error:', error);
      }
    );
    }    
}
