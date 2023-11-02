import { Component, OnInit } from '@angular/core';
import { Review } from '../interfaces/Review';
import { ReviewApiService } from '../services/review-api.service';
import { ClientService } from '../services/client.service';
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { FileHandle } from '../interfaces/file-handle.model';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { MatGridListModule } from '@angular/material/grid-list';



@Component({
  selector: 'app-review-submit',  // Specifies the selector for the component
  templateUrl: './review-submit.component.html',  // Specifies the template file for the component
  styleUrls: ['./review-submit.component.scss']  // Specifies the style files for the component
})
export class ReviewSubmitComponent implements OnInit {  // Definition of the ReviewSubmitComponent class which implements OnInit interface
  reviews: Review[] = [];
  selectedFiles: File[] = []; // Define selectedFiles as an array of File objects
  fileTypeError: string = '';

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

  constructor(private http: HttpClient, private sanitizer: DomSanitizer, private reviewApiService: ReviewApiService, private clientService: ClientService, private router: Router) {}  // Constructor for the ReviewSubmitComponent, injecting the required services

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

  uploadImages(files: File[]): Observable<string[]> {
    // Create an array to hold the image upload responses
    const imageUploadResponses: Observable<string>[] = [];
  
    for (const file of files) {
      const formData = new FormData();
      formData.append('image', file);
  
      // Push each upload response observable to the array
      imageUploadResponses.push(
        this.http.post('http://localhost:8080/reviews/image', formData, { responseType: 'text' })
      );
    }
  
    // Use forkJoin to execute all image uploads in parallel and wait for all responses
    return forkJoin(imageUploadResponses);
  }
  
  
  submitReview(): void {
    if (this.newReviewForm.valid) {
      // Check if an image file is selected
      if (this.selectedFiles) {
        this.uploadImages(this.selectedFiles).subscribe(
          (imageUploadResponse) => {
            // Image uploaded successfully, you can use imageUploadResponse to get the image reference or URL
            
            // Now, proceed to submit the review
            this.processReviewSubmission(imageUploadResponse);
          },
          (imageUploadError) => {
            console.error('Error uploading image:', imageUploadError);
            // Handle errors during image upload
          }
        );
      } else {
        // If no image is selected, proceed to submit the review without an image
        this.processReviewSubmission(null);
      }
    }
  }
  
  processReviewSubmission(imageReferences: string[] | null): void {
    const reviewFormData = this.prepareFormData(this.newReview);
  
    const currentDate = new Date();
    const formattedDate = currentDate.toLocaleString();
  
    this.newReviewForm.patchValue({ dateTime: formattedDate });
  
    const storedFirstName = localStorage.getItem('firstName');
    const storedLastName = localStorage.getItem('lastName');
  
    if (storedFirstName && storedLastName) {
      this.newReview.firstName = storedFirstName;
      this.newReview.lastName = storedLastName;
    }
  
    // Include the image references in the review data if available
    if (imageReferences && imageReferences.length > 0) {
      this.newReviewForm.patchValue({ imageReferences: imageReferences });
    }
  
    this.reviewApiService.addReview(this.newReviewForm.value).subscribe(
      (response) => {
        console.log('Review submitted successfully:', response);
      },
      (error) => {
        console.error('Error submitting review:', error);
      }
    );
  
    this.loadReviews();
    this.router.navigate(['/review-list']);
  }
  
  onFilesSelected(event: any) {
    const files: FileList | null = event.target?.files ?? null;
  
    if (files && files.length !== undefined) { // Use `!== undefined` check
      const fileCount = files.length;
  
      if (fileCount > 2) {
        this.fileTypeError = 'Only 2 files are allowed.';
        this.selectedFiles = []; // Clear the selected files
      } else {
        this.fileTypeError = ''; // Reset the error message
  
        const allowedTypes = ['.png', '.jpg', '.jpeg', '.pdf'];
        
  
        for (let i = 0; i < fileCount; i++) { // Use `fileCount`
          const selectedFile = files[i];
          const fileExtension = selectedFile?.name.split('.').pop()?.toLowerCase();
  
          if (!fileExtension || !allowedTypes.includes(`.${fileExtension}`)) {
            this.fileTypeError = 'Only PNG, JPG, JPEG, and PDF files are allowed.';
            
            break;
          }
  
          this.selectedFiles.push(selectedFile);
  
          if (this.selectedFiles.length >= 2) {
            break; // Stop after adding 2 files
          }
        }
      }
    }
  }
  
  
  
  
  


  

 removeImages(index: number) {
      this.selectedFiles.splice(index, 1); // Remove the image at the specified index
    
  }
  
  getSafeURL(file: File): any {
    return this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(file));
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
