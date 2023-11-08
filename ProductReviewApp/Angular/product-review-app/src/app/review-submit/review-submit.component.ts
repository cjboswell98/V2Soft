import { Component, OnInit } from '@angular/core';
import { Review } from '../interfaces/Review';
import { ReviewApiService } from '../services/review-api.service';
import { ClientService } from '../services/client.service';
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl, NgForm, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { FileHandle } from '../interfaces/file-handle.model';
import { DomSanitizer } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent } from '@angular/common/http';
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
  fileTypeErrorUnsupported: boolean = false;
  fileTypeErrorLimit: boolean = false;

  

  newReview: Review = {  // Object of type Review for holding the new review data
    reviewId: '',  // Initialize the reviewId to an empty string
    clientId: '',
    productName: '',  // Initialize the productName to an empty string
    firstName: '',  // Initialize the firstName to an empty string
    lastName: '',  // Initialize the lastName to an empty string
    email: '',
    zipCode: 0,  // Initialize the zipCode to 0
    rateCode: 0,  // Initialize the rateCode to 0
    comments: '',  // Initialize the comments to an empty string
    dateTime: '',  // Initialize the dateTime to an empty string
    reviewImages: [],
    reviewImage: '',
    
  };

  newReviewForm: FormGroup = new FormGroup({});  // Initialize the newReviewForm as a FormGroup

  constructor(
    private http: HttpClient, 
    private sanitizer: DomSanitizer, 
    private reviewApiService: ReviewApiService, 
    private clientService: ClientService, 
    private router: Router,
    private formBuilder: FormBuilder, ) {
      this.newReviewForm = this.formBuilder.group({
        // Your other form controls
        reviewImages: [], // Initialize as an empty array
      });
    }  // Constructor for the ReviewSubmitComponent, injecting the required services

 ngOnInit(): void {
  // Retrieve the image file names from local storage
  const imageFileNames = localStorage.getItem('imageFileNames');

  

  this.newReviewForm = new FormGroup({
    clientId: new FormControl(localStorage.getItem('clientId')),
    productName: new FormControl('', Validators.required),
    firstName: new FormControl(localStorage.getItem('firstName')),
    lastName: new FormControl(localStorage.getItem('lastName')),
    email: new FormControl('', Validators.required),
    zipCode: new FormControl('', [Validators.required, this.validateZipCode()]),
    rateCode: new FormControl('', Validators.required),
    comments: new FormControl(''),
    dateTime: new FormControl(''),
    reviewImage: new FormControl()
  });

  this.fetchMostRecentClient();
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
    localStorage.removeItem('imageFileNames');
    localStorage.removeItem('imageFileData');

    // Redirect to the login page or another destination
    this.router.navigate(['/login']);
  }

  async uploadImages(selectedFiles: File[]): Promise<void> {
    const reviewImageControl = this.newReviewForm.get('reviewImage');
    
    if (reviewImageControl) {
      for (const file of selectedFiles) {
        const formData = new FormData();
        formData.append('image', file);
      
        // Log the file being uploaded
        console.log(file.name);
      
        // Append the file name to the reviewImage form control
        const currentReviewImageValue = reviewImageControl.value || '';
        if (currentReviewImageValue) {
          reviewImageControl.setValue(currentReviewImageValue + ', ' + file.name);
        } else {
          reviewImageControl.setValue(file.name);
        }
      
        // Send the image upload request and store the response
        const uploadResponse = this.http.post<FileHandle>('http://localhost:8080/reviews/image', formData);
        // Wait for the response before proceeding
        await uploadResponse.toPromise();
      }
    }
  }
  
  
  async submitReview(): Promise<void> {
    if (this.newReviewForm.valid) {
      if (this.selectedFiles && this.selectedFiles.length > 0) {
        try {
          await this.uploadImages(this.selectedFiles);
          // Now, the image data is saved, and you can proceed to submit the review
          this.processReviewSubmission();
        } catch (error) {
          console.error('Error saving image data:', error);
        }
      } else {
        this.processReviewSubmission();
      }
    }
  }
  
  
  
  
  processReviewSubmission(): void {
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
  
    // Retrieve the image references from local storage
    const imageReferences: FileHandle[] | null = JSON.parse(localStorage.getItem('imageFileData') ?? 'null');

  
    // Include the image references in the review data if available
    if (imageReferences && imageReferences.length > 0) {
      this.newReview.reviewImages = imageReferences;
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
    const files: FileList = event.target.files;
    const allowedFileTypes = ['image/png', 'image/jpeg', 'image/jpg', 'application/pdf'];
  
    this.fileTypeErrorUnsupported = false;
    this.fileTypeErrorLimit = false;
  
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
  
      if (!allowedFileTypes.includes(file.type)) {
        this.fileTypeErrorUnsupported = true;
      }
  
      if (this.selectedFiles.length >= 2) {
        this.fileTypeErrorLimit = true;
      }
  
      if (this.selectedFiles.length < 2 && allowedFileTypes.includes(file.type)) {
        this.selectedFiles.push(file);
  
        // Add the file name to the imageName property in the newReview object
        this.newReview.reviewImage = file.name;
  
        // Save the file name to local storage
        const uploadedFileNames = JSON.parse(localStorage.getItem('uploadedFiles') || '[]');
        uploadedFileNames.push(file.name);
        localStorage.setItem('uploadedFiles', JSON.stringify(uploadedFileNames));
      }
    }
  }
  
  
  


  fileDropped(fileHandle: FileHandle) {
    if (fileHandle.file) {
      this.selectedFiles.push(fileHandle.file);
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
        'image',
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
