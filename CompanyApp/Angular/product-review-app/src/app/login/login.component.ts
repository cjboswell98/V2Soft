import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AbstractControl, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
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
          this.loginSuccess = true; // Set loginSuccess to true on successful login
          this.router.navigate(['/review-list']);
        } else {
          console.error('Authentication failed:', response);
          this.loginSuccess = false; // Set loginSuccess to false on failed login
        }
      },
      (error) => {
        console.error('Error:', error);
        this.loginSuccess = false; // Set loginSuccess to false if an error occurs
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
