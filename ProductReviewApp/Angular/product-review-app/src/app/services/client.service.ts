import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../interfaces/Client';

@Injectable({
  providedIn: 'root'  // Specifies that the service should be provided in the root injector
})
export class ClientService {  // Definition of the ClientService class

  private apiUrl = 'http://localhost:8080/reviews';  // Setting the base API URL for the reviews

  constructor(private http: HttpClient) { }  // Constructor for the ClientService, injecting the HttpClient

  getMostRecentClient(): Observable<Client> {  // Method to get the most recent client from the API
    // Make an HTTP GET request to fetch the most recent client
    return this.http.get<Client>(`${this.apiUrl}/viewClients`);
  }
}
