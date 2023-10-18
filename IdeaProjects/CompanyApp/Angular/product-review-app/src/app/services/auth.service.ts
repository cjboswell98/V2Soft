// import { Injectable } from '@angular/core';
// import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { Observable } from 'rxjs';

// @Injectable({
//   providedIn: 'root'
// })
// export class AuthService {
//   private apiUrl = 'http://localhost:4200/review-list';
//   private token: string;

//   constructor(private http: HttpClient) {}

//   login(username: string, password: string): Observable<any> {
//     const body = { username, password };
//     return this.http.post(`${this.apiUrl}/login`, body);
//   }

//   setToken(token: string) {
//     this.token = token;
//     localStorage.setItem('jwtToken', token);
//   }

//   getToken(): string {
//     return this.token || localStorage.getItem('jwtToken');
//   }

//   getHeaders(): HttpHeaders {
//     return new HttpHeaders({
//       Authorization: `Bearer ${this.getToken()}`
//     });
//   }
// }
