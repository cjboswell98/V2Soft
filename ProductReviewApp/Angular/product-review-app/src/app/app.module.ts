import { NgModule } from '@angular/core'; 
import { BrowserModule } from '@angular/platform-browser'; 
import { HttpClientModule } from '@angular/common/http';  

import { AppComponent } from './app.component';
import { ReviewListComponent } from './review-list/review-list.component'; 
import { ReviewSubmitComponent } from './review-submit/review-submit.component';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';  
import { MatCardModule } from '@angular/material/card';  
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { AdminReviewListComponent } from './admin-review-list/admin-review-list.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './auth-guard/auth.guard';
import { MatGridListModule } from '@angular/material/grid-list';
import { DragDirective } from './drag/drag.directive';
import { ShowImagesComponent } from './show-images/show-images.component';


const routes: Routes = [  // Defining an array of route configurations
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },  // Redirecting the empty path to the 'review-list' path
  { path: 'review-list', component: ReviewListComponent, canActivate: [AuthGuard]  },  // Configuring the 'review-list' path to use the ReviewListComponent
  { path: 'admin-review-list', component: AdminReviewListComponent, canActivate: [AuthGuard]  },
  { path: 'review-submit', component: ReviewSubmitComponent, canActivate: [AuthGuard]},  // Configuring the 'review-submit' path to use the ReviewSubmitComponent
  { path: 'register', component: RegisterComponent },  // Configuring the 'register' path to use the RegisterComponent
  { path: 'login', component: LoginComponent },  // Configuring the 'login' path to use the LoginComponent
  // Add more routes as needed for other views/components
];

@NgModule({
  declarations: [  // Array of components declared in this module
    AppComponent,  // The main application component
    ReviewListComponent,
    HomeComponent,
    AdminReviewListComponent,  // The component for the review list
    ReviewSubmitComponent,  // The component for submitting reviews
    LoginComponent,  // The component for user login
    RegisterComponent, HomeComponent, DragDirective, ShowImagesComponent,  // The component for user registration
  ],
  imports: [  // Array of modules this module depends on
    BrowserModule,  // The browser module for use in the browser
    HttpClientModule,  // The HTTP module for making HTTP requests
    FormsModule,  // The module for managing forms in the application
    ReactiveFormsModule,  // The module for reactive forms in the application
    RouterModule.forRoot(routes),  // The module for managing application routes
    BrowserAnimationsModule,  // The module for providing support for animations
    MatToolbarModule, MatGridListModule, MatIconModule, MatButtonModule, MatSelectModule,MatInputModule, MatCardModule, MatTableModule, MatSortModule, MatPaginatorModule // Angular Material modules for various UI components
  ],
  exports: [RouterModule, MatToolbarModule, MatIconModule, MatButtonModule, MatInputModule, MatCardModule],  // Array of modules to export from this module
  providers: [],  // Array of services provided by this module
  bootstrap: [AppComponent]  // The main component that should be bootstrapped
})
export class AppModule { }  // The main class representing the root module of the application
