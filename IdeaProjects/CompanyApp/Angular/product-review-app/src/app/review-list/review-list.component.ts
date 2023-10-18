import { Component, OnInit } from '@angular/core';
import { ReviewApiService } from '../services/review-api.service';
import { Review } from '../interfaces/Review';

@Component({
  selector: 'app-review-list',
  templateUrl: './review-list.component.html',
  styleUrls: ['./review-list.component.scss']
})
export class ReviewListComponent implements OnInit {
  reviews: any[] = [];
  eD: Review | undefined;

  constructor(private apiService: ReviewApiService) {}

  ngOnInit(): void {
    this.refreshPage();
  }

  loadReviews(): void {
    this.apiService.getAllReviews().subscribe((data: Review[]) => {
      this.reviews = data;
    });
  }

  refreshPage(): void {
    this.loadReviews();
    // You can add any other necessary logic to refresh the page here
  }
}
