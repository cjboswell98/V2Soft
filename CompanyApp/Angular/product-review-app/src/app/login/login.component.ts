import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AbstractControl, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Client } from '../interfaces/Client';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  clients: Client[] = [];
  firstName: string = '';
  lastName: string = '';
  username: string = "";
  password: string = "";
  newReviewForm: FormGroup = new FormGroup({});
  loginSuccess: boolean = true; // Initialize loginSuccess as true

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.newReviewForm = new FormGroup({
      username: new FormControl('', [Validators.required, this.validateUsername()]), // Use custom validator for username
      password: new FormControl('', [Validators.required, this.validatePassword()]) // Use custom validator for password
    });
  }

  onSubmit() {
    const body = {
      username: this.username,
      password: this.password
    };

    this.http.post('http://localhost:8080/reviews/verifyLogin', body, { responseType: 'text' }).subscribe(
      (response) => {
        if (response === 'Login successful') {
          console.log('Login successful', response);

           // Clear the local storage for firstName and lastName
        localStorage.removeItem('firstName');
        localStorage.removeItem('lastName');

          this.router.navigate(['/home']);

          // Fetch the first name and last name using the username
          this.fetchFirstNameAndLastName(this.username);
        } else {
          console.error('Authentication failed:', response);
          // Handle authentication failure
        }
      },
      (error) => {
        console.error('Error:', error);
        // Handle error
      }
    );
  }

  fetchFirstNameAndLastName(username: string) {
    this.http.get<Client[]>(`http://localhost:8080/reviews/viewClients`).subscribe(
      (clients) => {
        const matchingClient = clients.find(client => client.username === username);
        if (matchingClient) {
          // Save the first name and last name to local storage
          localStorage.setItem('firstName', matchingClient.firstName);
          localStorage.setItem('lastName', matchingClient.lastName);
        } else {
          // Handle the case where no matching client was found
          console.error('No matching client found for username: ', username);
          // You can optionally clear the previous values from local storage here.
          localStorage.removeItem('firstName');
          localStorage.removeItem('lastName');
        }
      },
      (error) => {
        console.error('Failed to fetch client data:', error);
        // Handle error
      }
    );
  }
  
  


  // Validation functions for username and password
  validateUsername(): ValidatorFn {
    return (control: AbstractControl) => {
      const value = control.value as string;
      if (value.length < 5) {
        return { minlength: true };
      }
      return null;
    };
  }

  validatePassword(): ValidatorFn {
    return (control: AbstractControl) => {
      const value = control.value as string;
      if (value.length < 5) {
        return { minlength: true };
      }
      return null;
    };
  }
}
