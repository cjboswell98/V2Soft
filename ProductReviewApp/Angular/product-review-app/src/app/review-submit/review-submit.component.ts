import { Component, OnInit } from '@angular/core';
import { Review } from '../interfaces/Review';
import { ReviewApiService } from '../services/review-api.service';
import { ClientService } from '../services/client.service';
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { FileHandle } from '../interfaces/file-handle.model';
import { DomSanitizer } from '@angular/platform-browser';


@Component({
  selector: 'app-review-submit',  // Specifies the selector for the component
  templateUrl: './review-submit.component.html',  // Specifies the template file for the component
  styleUrls: ['./review-submit.component.scss']  // Specifies the style files for the component
})
export class ReviewSubmitComponent implements OnInit {  // Definition of the ReviewSubmitComponent class which implements OnInit interface
  reviews: Review[] = [];

  newReview: Review = {  // Object of type Review for holding the new review data
    reviewId: '',  // Initialize the reviewId to an empty string
    clientId: '',
    productName: '',  // Initialize the productName to an empty string
    firstName: '',  // Initialize the firstName to an empty string
    lastName: '',  // Initialize the lastName to an empty string
    zipCode: 0,  // Initialize the zipCode to 0
    rateCode: 0,  // Initialize the rateCode to 0
    comments: '',  // Initialize the comments to an empty string
    dateTime: '',  // Initialize the dateTime to an empty string
    reviewImages: []
  };

  newReviewForm: FormGroup = new FormGroup({});  // Initialize the newReviewForm as a FormGroup

  constructor(private sanitizer: DomSanitizer, private reviewApiService: ReviewApiService, private clientService: ClientService, private router: Router) {}  // Constructor for the ReviewSubmitComponent, injecting the required services

  ngOnInit(): void {  // Angular lifecycle hook OnInit
    this.newReviewForm = new FormGroup({  // Initialize the newReviewForm FormGroup with form controls

      clientId: new FormControl(localStorage.getItem('clientId')),
      productName: new FormControl('', Validators.required),  // Add the product name form control with necessary validators
      firstName: new FormControl(localStorage.getItem('firstName')),  // Add the first name form control with necessary validators
      lastName: new FormControl(localStorage.getItem('lastName')),  // Add the last name form control with necessary validators
      zipCode: new FormControl('', [Validators.required, this.validateZipCode()]),  // Add the zip code form control with necessary validators
      rateCode: new FormControl('', Validators.required),  // Add the rate code form control with necessary validators
      comments: new FormControl(''),  // Add the comments form control with necessary validators
      dateTime: new FormControl(''),  // Add the dateTime form control
    });

    this.fetchMostRecentClient();  // Call the function to fetch the most recent client
  }

  fetchMostRecentClient(): void {  // Method to fetch the most recent client
    this.clientService.getMostRecentClient().subscribe(  // Subscribe to the client service for the most recent client
      (client) => {  // Handle the successful response
        this.newReview.id = client.id;  // Set the id of the new review from the fetched client dataz
      },
      (error) => {  // Handle the error response
        console.error('Error fetching most recent client:', error);  // Log the error message to the console
      }
    );
  }

  logout() {
    // Remove the login status from local storage
    localStorage.removeItem('loginStatus');

    // Redirect to the login page or another destination
    this.router.navigate(['/login']);
  }

  submitReview(): void {
    
    const reviewFormData = this.prepareFormData(this.newReview);

    if (this.newReviewForm.valid) {
      const currentDate = new Date();
      const formattedDate = currentDate.toLocaleString();

      this.newReviewForm.patchValue({ dateTime: formattedDate });
      // Retrieve the first name and last name from local storage
      const storedFirstName = localStorage.getItem('firstName');
      const storedLastName = localStorage.getItem('lastName');
  
      if (storedFirstName && storedLastName) {
        // Update the new review object with the retrieved names
        this.newReview.firstName = storedFirstName;
        this.newReview.lastName = storedLastName;
      }
  
      this.reviewApiService.addReview(this.newReviewForm.value).subscribe(
        (response) => {
          console.log('Review submitted successfully:', response);
          // Save the names to the database along with the review
          this.reviewApiService.addReview({
            ...this.newReviewForm.value,
            firstName: storedFirstName,
            lastName: storedLastName
          }).subscribe(
            (response) => {
              console.log('Names saved along with review:', response);
            },
            (error) => {
              console.error('Error saving names with review:', error);
            }
          );
        },
        (error) => {
          console.error('Error submitting review:', error);
        }
      );
      this.loadReviews();
      this.router.navigate(['/review-list']);
    }
  }

  onFileSelected(event: any) {
    if(event.target.files) {
      const file = event.target.files[0];

      const fileHandle: FileHandle = {
        file: file,
        url: this.sanitizer.bypassSecurityTrustUrl(
          window.URL.createObjectURL(file)
        )
      }

      this.newReview.reviewImages.push(fileHandle);
    }
  }

  prepareFormData(newReview: Review): FormData {
    const formData = new FormData();

    formData.append(
      'review',
      new Blob([JSON.stringify(newReview)], {type: 'application/json'})
    );

    for(var i = 0; i < newReview.reviewImages.length; i++) {
      formData.append(
        'imageFile',
        newReview.reviewImages[i].file,
        newReview.reviewImages[i].file.name
      );
    }

    return formData;
  }
  
  loadReviews(): void {
    this.reviewApiService.getAllReviews().subscribe((data: Review[]) => {
      this.reviews = data;
    });
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
