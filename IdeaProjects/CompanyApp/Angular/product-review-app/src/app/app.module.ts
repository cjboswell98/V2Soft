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
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';


const routes: Routes = [
  { path: '', redirectTo: 'review-list', pathMatch: 'full' },
  { path: 'review-list', component: ReviewListComponent },
  { path: 'review-submit', component: ReviewSubmitComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  // Add more routes as needed for other views/components
];

@NgModule({
  declarations: [
    AppComponent,
    ReviewListComponent,
    ReviewSubmitComponent,
    LoginComponent,
    RegisterComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule, // Add ReactiveFormsModule here
    RouterModule.forRoot(routes), BrowserAnimationsModule,MatToolbarModule, MatIconModule, MatButtonModule, MatInputModule, MatCardModule
  ],
  exports: [RouterModule, MatToolbarModule, MatIconModule, MatButtonModule, MatInputModule, MatCardModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
