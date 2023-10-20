import { Component, OnInit } from '@angular/core';
import { Review } from '../interfaces/Review';
import { ReviewApiService } from '../services/review-api.service';
import { ClientService } from '../services/client.service';
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
@Component({
  selector: 'app-review-submit',  // Specifies the selector for the component
  templateUrl: './review-submit.component.html',  // Specifies the template file for the component
  styleUrls: ['./review-submit.component.scss']  // Specifies the style files for the component
})
export class ReviewSubmitComponent implements OnInit {  // Definition of the ReviewSubmitComponent class which implements OnInit interface

  newReview: Review = {  // Object of type Review for holding the new review data
    reviewId: '',  // Initialize the reviewId to an empty string
    productName: '',  // Initialize the productName to an empty string
    firstName: '',  // Initialize the firstName to an empty string
    lastName: '',  // Initialize the lastName to an empty string
    zipCode: 0,  // Initialize the zipCode to 0
    rateCode: 0,  // Initialize the rateCode to 0
    comments: '',  // Initialize the comments to an empty string
    dateTime: '',  // Initialize the dateTime to an empty string
  };

  newReviewForm: FormGroup = new FormGroup({});  // Initialize the newReviewForm as a FormGroup

  constructor(private reviewApiService: ReviewApiService, private clientService: ClientService, private router: Router) {}  // Constructor for the ReviewSubmitComponent, injecting the required services

  ngOnInit(): void {  // Angular lifecycle hook OnInit
    this.newReviewForm = new FormGroup({  // Initialize the newReviewForm FormGroup with form controls

      productName: new FormControl('', Validators.required),  // Add the product name form control with necessary validators
      firstName: new FormControl('', [Validators.required, Validators.pattern('^[a-zA-Z]+')]),  // Add the first name form control with necessary validators
      lastName: new FormControl('', [Validators.required, Validators.pattern('^[a-zA-Z]+')]),  // Add the last name form control with necessary validators
      zipCode: new FormControl('', [Validators.required, this.validateZipCode()]),  // Add the zip code form control with necessary validators
      rateCode: new FormControl(0, Validators.required),  // Add the rate code form control with necessary validators
      comments: new FormControl(''),  // Add the comments form control with necessary validators
      dateTime: new FormControl(''),  // Add the dateTime form control
    });

    this.fetchMostRecentClient();  // Call the function to fetch the most recent client
  }

  fetchMostRecentClient(): void {  // Method to fetch the most recent client
    this.clientService.getMostRecentClient().subscribe(  // Subscribe to the client service for the most recent client
      (client) => {  // Handle the successful response
        this.newReview.id = client.id;  // Set the id of the new review from the fetched client data
      },
      (error) => {  // Handle the error response
        console.error('Error fetching most recent client:', error);  // Log the error message to the console
      }
    );
  }

  submitReview(): void {  // Method to submit the review
    if (this.newReviewForm.valid) {  // Check if the form is valid
      const currentDate = new Date();  // Get the current date and time
      this.newReviewForm.patchValue({ dateTime: currentDate.toISOString() });  // Set the dateTime field in the form

      this.reviewApiService.addReview(this.newReviewForm.value).subscribe(  // Subscribe to the review API service for adding the new review
        (response) => {  // Handle the successful response
          console.log('Review submitted successfully:', response);  // Log the success message to the console
        },
        (error) => {  // Handle the error response
          console.error('Error submitting review:', error);  // Log the error message to the console
        }
      );
      this.router.navigate(['/review-list']);  // Redirect to the review list page on button press
    }
  }

  onCommentsChange(event: any): void {
    const target = event.target as HTMLTextAreaElement;
    if (target) {
        const text = target.value;
        this.newReview.comments = text ? (text.length > 500 ? text.substring(0, 500) : text) : '';
    }
}

  validateZipCode(): ValidatorFn {  // Method to validate the zip code
    return (control: AbstractControl): { [key: string]: any } | null => {  // Return a function that validates the control
      const valid = /^\d{5}(\d{4})?$/.test(control.value);  // Check if the zip code matches the required pattern
      return valid ? null : { invalidZipCode: true };  // Return an object if the zip code is invalid
    };
  }
}
