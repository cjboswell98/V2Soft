import { CanActivateFn } from '@angular/router';

export const AuthGuard: CanActivateFn = (route, state) => {
  // Check if the login status is "Login Successful" in local storage
  if (localStorage.getItem('loginStatus') === 'Login Successful') {
    return true; // Allow access to the route
  } else {
    return false; // Deny access to the route
  }
};

