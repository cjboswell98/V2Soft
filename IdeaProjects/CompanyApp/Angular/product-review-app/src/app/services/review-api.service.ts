import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../interfaces/Review';

@Injectable({
  providedIn: 'root'  // Specifies that the service should be provided in the root injector
})
export class ReviewApiService {  // Defining the ReviewApiService class
  apiUrl = 'http://localhost:8080/reviews';  // Setting the base API URL for the reviews
  viewReviewsUrl: string = this.apiUrl + "/viewReviews/reviews";  // Constructing the URL for viewing reviews

  constructor(private http: HttpClient) {}  // Constructor for the ReviewApiService, injecting the HttpClient

  getAllReviews(): Observable<Review[]> {  // Method to get all reviews from the API
    return this.http.get<Review[]>(this.viewReviewsUrl);  // Making a GET request to retrieve all reviews
  }

  addReview(newReview: Review): Observable<any> {  // Method to add a new review to the API
    return this.http.post(this.apiUrl + '/addReview', newReview);  // Making a POST request to add a new review
  }
}
