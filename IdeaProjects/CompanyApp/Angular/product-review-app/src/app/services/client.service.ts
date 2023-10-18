import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../interfaces/Client';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = 'http://localhost:8080/reviews'; // Update with the base API URL

  constructor(private http: HttpClient) { }

  getMostRecentClient(): Observable<Client> {
    // Make an HTTP request to fetch the most recent client
    return this.http.get<Client>(`${this.apiUrl}/viewClients`);
  }
}
