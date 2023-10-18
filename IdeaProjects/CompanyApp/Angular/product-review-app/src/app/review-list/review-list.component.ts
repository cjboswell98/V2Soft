import { Component, OnInit } from '@angular/core';
import { ReviewApiService } from '../services/review-api.service';
import { Review } from '../interfaces/Review';
import { trigger, state, style, transition, animate } from '@angular/animations';


@Component({
  selector: 'app-review-list',
  templateUrl: './review-list.component.html',
  styleUrls: ['./review-list.component.scss'], 
  animations: [
    trigger('clicked', [
      state('before', style({
        backgroundColor: 'white'
      })),
      state('after', style({
        backgroundColor: 'yellow'
      })),
      transition('before => after', animate('300ms ease-in')),
      transition('after => before', animate('300ms ease-out'))
    ])
  ]
})
export class ReviewListComponent implements OnInit {
  reviews: Review[] = [];
  pagedReviews: Review[] = [];
  selectedPageSize: number = 10;
  selectedProduct: string = '';
  rateCodeSearch: string = '';
  reviewIdSearch: string = '';
  showSortingOptions: boolean = false; // Flag to toggle sorting options
  selectedHeader: string = '';
  isAscending: any;

  constructor(private apiService: ReviewApiService) {}

  ngOnInit(): void {
    this.refreshPage();
  }

  loadReviews(): void {
    this.apiService.getAllReviews().subscribe((data: Review[]) => {
      this.reviews = data;
      this.updatePagedReviews();
    });
  }

  refreshPage(): void {
    this.loadReviews();
    this.updatePagedReviews();
  }

  updatePagedReviews(): void {
    const startIndex = 0; // Define the start index for pagination
    this.pagedReviews = this.reviews.slice(startIndex, startIndex + this.selectedPageSize);
  }

  onPageSizeChange(): void {
    this.updatePagedReviews();
  }

  onProductFilterChange(): void {
    if (this.selectedProduct !== '') {
      this.pagedReviews = this.reviews.filter(review => review.productName === this.selectedProduct);
    } else {
      this.refreshPage();
    }
  }

  onRateCodeSearch(): void {
    if (this.rateCodeSearch !== '') {
      this.pagedReviews = this.reviews.filter(review => review.rateCode.toString() === this.rateCodeSearch);
    } else {
      this.refreshPage();
    }
  }

  onReviewIdSearch(): void {
    if (this.reviewIdSearch !== '') {
      this.pagedReviews = this.reviews.filter(review => review.reviewId.toString() === this.reviewIdSearch);
    } else {
      this.refreshPage();
    }
  }
  

  toggleSortingOptions(): void {
    this.showSortingOptions = !this.showSortingOptions;
  }

  sort(key: keyof Review): void {
    const isAscending = this.selectedHeader === key ? !this.isAscending : true; // Toggle the sorting order if the same header is clicked again
    this.selectedHeader = key;
  
    this.pagedReviews.sort((a, b) => {
      let comparison = 0;
  
      if (key === 'reviewId') {
        const valueA = parseInt(a[key] as string);
        const valueB = parseInt(b[key] as string);
  
        comparison = valueA - valueB;
      } else {
        const valueA = a[key] as string | Date;
        const valueB = b[key] as string | Date;
  
        if (valueA > valueB) {
          comparison = 1;
        } else if (valueA < valueB) {
          comparison = -1;
        }
      }
  
      return isAscending ? comparison : comparison * -1;
    });
  
    this.isAscending = isAscending; // Store the current sorting order
  }
  
  
}
