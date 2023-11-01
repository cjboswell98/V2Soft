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
  role: string = '';
  newReviewForm: FormGroup = new FormGroup({});
  loginSuccess: boolean = true; // Initialize loginSuccess as true
  showWrongPasswordMessage: boolean = true;
  errorMessage: string = '';
  clientId!: string;


  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.newReviewForm = new FormGroup({
      clientId: new FormControl(localStorage.getItem('clientId')),
      username: new FormControl('', [Validators.required, this.validateUsername()]), // Use custom validator for username
      password: new FormControl('', [Validators.required, this.validatePassword()]), // Use custom validator for password
      role: new FormControl(localStorage.getItem('role')),
    });
  }

  logout() {
    // Remove the login status from local storage
    localStorage.removeItem('loginStatus');

    // Redirect to the login page or another destination
    this.router.navigate(['/login']);
  }

  onSubmit() {
    const body = {
      clientId: this.clientId,
      username: this.username,
      password: this.password,
      role: this.role
    };
  
    this.http.post('http://localhost:8080/reviews/verifyLogin', body, { responseType: 'text' }).subscribe(
      (response) => {
        if (response === 'Login successful') {
          console.log('Login successful', response);
  
          // Now that the client is verified on the endpoint, fetch the client's role
          this.fetchClientRole(this.username, () => {
            // Save the login status and first name in local storage
            localStorage.setItem('loginStatus', 'Login Successful');
            localStorage.setItem('firstName', this.firstName);
            localStorage.setItem('lastName', this.lastName);
            localStorage.setItem('clientId', this.clientId);
  
            if (this.role === "ADMIN") {
              this.router.navigate(['/admin-review-list']);
            } else {
              this.router.navigate(['/home']);
            }
          });
        } else {
          console.error('Authentication failed:', response);
          this.loginSuccess = false;
          // Handle authentication failure here, such as displaying an error message
        }
      },
      (error) => {
        console.error('Error:', error);
        this.showWrongPassword(this.username);
        // Handle error
      }
    );
  }
  
  fetchClientRole(username: string, callback: () => void) {
    this.http.get<Client[]>(`http://localhost:8080/reviews/viewClients`).subscribe(
      (clients) => {
        const matchingClient = clients.find(client => client.username === username);
        if (matchingClient) {
          this.firstName = matchingClient.firstName;
          this.lastName = matchingClient.lastName;
          this.role = matchingClient.role; // Fetch the client's role
        }
        callback();
      },
      (error) => {
        console.error('Failed to fetch client data:', error);
        // Handle error
        callback();
      }
    );
  }
  
  

  showWrongPassword(username: string) {
    this.showWrongPasswordMessage = true;
    this.loginSuccess = false;
    this.errorMessage = `${username}`;
  }
  

  
  fetchFirstNameAndLastName(username: string, callback: () => void) {
    this.http.get<Client[]>(`http://localhost:8080/reviews/viewClients`).subscribe(
      (clients) => {
        const matchingClient = clients.find(client => client.username === username);
        if (matchingClient) {
          this.firstName = matchingClient.firstName;
          this.lastName = matchingClient.lastName;
          // You can also save the last name here if needed
        }
        callback();
      },
      (error) => {
        console.error('Failed to fetch client data:', error);
        // Handle error
        callback();
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
