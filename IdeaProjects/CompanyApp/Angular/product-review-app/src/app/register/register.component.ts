import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  username: string = '';
  password: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    const body = {
      username: this.username,
      password: this.password
    };

    this.http.post('http://localhost:8080/reviews/newClient', body, { responseType: 'text' }).subscribe(
      (response) => {
        if (response === 'Client successfully created') {
          console.log('Registration successful', response); // Log the success response
          this.router.navigate(['/login']); // Redirect to the login page after successful registration
        } else {
          console.error('Registration failed:', response);
          // Handle registration failure here, such as displaying an error message
        }
      },
      (error) => {
        console.error('Error:', error);
      }
    );
  }
}
