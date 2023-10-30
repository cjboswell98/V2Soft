import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../interfaces/Review';
import { Client } from '../interfaces/Client';
import { Jwt } from '../interfaces/Jwt';

@Injectable({
  providedIn: 'root'
})
export class ReviewApiService {
  apiUrl = 'http://localhost:8080/reviews';
  viewReviewsUrl: string = `${this.apiUrl}/viewReviews/reviews`;
  viewClientsUrl: string = `${this.apiUrl}/viewClients`;
  deleteReviewsUrl: string = `${this.apiUrl}/deleteReview`;
  viewJwt: string = 'http://localhost:8080/authenticate';

  constructor(private http: HttpClient) {}

  getAllReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(this.viewReviewsUrl);
  }

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.viewClientsUrl);
  }

  getJwt(jwtToken: string): Observable<Jwt[]> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${jwtToken}`
    });

    return this.http.get<Jwt[]>(this.viewJwt, { headers });
  }

  addReview(newReview: Review): Observable<any> {
    return this.http.post(`${this.apiUrl}/addReview`, newReview);
  }

  public deleteReview(reviewId: number) {
    return this.http.delete("http://localhost:8080/reviews/deleteReview/" + reviewId);
  }

}
