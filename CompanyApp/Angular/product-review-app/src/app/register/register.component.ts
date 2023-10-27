import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  firstName: string = '';
  lastName: string = '';
  username: string = '';
  password: string = '';

  clients: any[] = []; // Holds the data retrieved from the endpoint

  constructor(private http: HttpClient, private router: Router) {
    // Fetch all clients data upon initialization
    this.fetchClientsData();
  }

  fetchClientsData(): void {
    this.http.get('http://localhost:8080/reviews/viewClients').subscribe((data: any) => {
      this.clients = data;
    });
  }

  onSubmit() {
    const body = {
      firstName: this.firstName,
      lastName: this.lastName,
      username: this.username,
      password: this.password
    };
  
    // Check if the username already exists
    if (this.isUsernameExists(this.username)) {
      console.error('Username already exists. Please choose a different username.');
      // Handle the case where the username already exists
    } else {
      // If the username is unique, proceed with the registration
      this.registerUser(body);
  
    }
  }
  

  isUsernameAdmin(username: string): boolean {
    return username === 'admin';
  }  

  isUsernameExists(username: string): boolean {
    return this.clients.some(client => client.username === username);
  }

  registerUser(body: any) {
    // Make the registration request
    this.http.post('http://localhost:8080/reviews/newClient', body, { responseType: 'text' }).subscribe(
      (response) => {
        if (response === 'Client successfully created') {
          console.log('Registration successful', response); // Log the success response
  
          // Save the first name and last name to local storage
          localStorage.setItem('firstName', this.firstName);
          localStorage.setItem('lastName', this.lastName);
  
          if (localStorage.getItem("firstName") === "admin") {
            this.router.navigate(['/admin-review-list']);
          } else {
            // Redirect to another route if not "admin"
            this.router.navigate(['/home']);
          }
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
