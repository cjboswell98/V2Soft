import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
    constructor() {}

    public setRoles(roles: []) {
        localStorage.setItem('roles', JSON.stringify(roles));
    }

    // public getRoles(): [] {
    //     return JSON.parse(localStorage.getItem('roles'));
    // }

    setToken(jwtToken: string) {
        localStorage.setItem('jwtToken', jwtToken);
    }

    getToken() {
        return localStorage.getItem('jwtToken');
    }

    public clear() {
        localStorage.clear();
    }

    // public isLoggedIn() {
    //     return this.getRoles() && this.getToken();
    // }
}