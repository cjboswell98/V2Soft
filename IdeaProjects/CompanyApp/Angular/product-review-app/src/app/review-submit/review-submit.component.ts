import { Component, OnInit } from '@angular/core';
import { Review } from '../interfaces/Review';
import { ReviewApiService } from '../services/review-api.service';
import { ClientService } from '../services/client.service';
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-review-submit',
  templateUrl: './review-submit.component.html',
  styleUrls: ['./review-submit.component.scss']
})
export class ReviewSubmitComponent implements OnInit {
  newReview: Review = {
    reviewId: '',
    productName: '',
    firstName: '',
    lastName: '',
    zipCode: 0,
    rateCode: 0,
    comments: '',
    dateTime: '',
   
  };

  newReviewForm: FormGroup = new FormGroup({});

  constructor(private reviewApiService: ReviewApiService, private clientService: ClientService, private router: Router) {}

  ngOnInit(): void {
    this.newReviewForm = new FormGroup({
      
      productName: new FormControl('', Validators.required),
      firstName: new FormControl('', [Validators.required, Validators.pattern('^[a-zA-Z]+')]),
      lastName: new FormControl('', [Validators.required, Validators.pattern('^[a-zA-Z]+')]),
      zipCode: new FormControl('', [Validators.required, this.validateZipCode()]),
      rateCode: new FormControl(0, Validators.required),
      comments: new FormControl('', Validators.required),
      dateTime: new FormControl('', Validators.required),
    });

    this.fetchMostRecentClient();
  }

  fetchMostRecentClient(): void {
    this.clientService.getMostRecentClient().subscribe(
      (client) => {
        this.newReview.id = client.id;
      },
      (error) => {
        console.error('Error fetching most recent client:', error);
      }
    );
  }

  submitReview(): void {
    if (this.newReviewForm.valid) {
      this.reviewApiService.addReview(this.newReviewForm.value).subscribe(
        (response) => {
          console.log('Review submitted successfully:', response);
        },
        (error) => {
          console.error('Error submitting review:', error);
        }
      );
      this.router.navigate(['/review-list']); // Redirect to the review list page on button press
    }
}


  validateZipCode(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const valid = /^\d{5}(\d{4})?$/.test(control.value);
      return valid ? null : { invalidZipCode: true };
    };
  }
}
