import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../interfaces/Review';

@Injectable({
  providedIn: 'root'
})
export class ReviewApiService {
  apiUrl = 'http://localhost:8080/reviews';
  viewReviewsUrl: string = this.apiUrl + "/viewReviews/reviews"; // Corrected URL

  constructor(private http: HttpClient) {}

  getAllReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(this.viewReviewsUrl); // Use the corrected URL here
  }

  addReview(newReview: Review): Observable<any> {
    return this.http.post(this.apiUrl + '/addReview', newReview);
  }
}
