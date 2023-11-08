
import { ReviewApiService } from '../services/review-api.service';
import { Review } from '../interfaces/Review';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map } from 'rxjs/operators';
import { ResponseHandlerService } from '../services/response-handler.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Jwt } from '../interfaces/Jwt';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';


@Component({
  selector: 'app-review-list',
  templateUrl: './review-list.component.html',
  styleUrls: ['./review-list.component.scss'], 
  animations: []
})
export class ReviewListComponent implements OnInit {
  reviews: Review[] = [];
  pagedReviews: Review[] = [];
  selectedPageSize: number = 10;
  selectedProduct: string = '';
  rateCodeSearch: string = '';
  reviewIdSearch: string = '';
  showSortingOptions: boolean = false; // Flag to toggle sorting options
  selectedHeader: string = '';
  desktopView: any;
  isMobileView: boolean = false;
  loggedInUser: any;
  userFirstName: string = localStorage.getItem('firstName') || ''; // Replace 'John' with a default name
 
  @ViewChild(MatSort) sort: MatSort | undefined;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource: any;
  username: string | undefined;
  jwtToken: string = '';

  constructor(
    private breakpointObserver: BreakpointObserver,
    private apiService: ReviewApiService, 
    public responseHandlerService: ResponseHandlerService,
    private router: Router,
    private http: HttpClient
  ) {}


  displayedColumns: string[] = ['reviewId', 'productName', 'firstName', 'lastName', 'zipCode', 'rateCode', 'comments', 'images', 'dateTime'];

  // Initializes the component and refreshes the page
  ngOnInit(): void {
    this.login();
    this.dataSource = new MatTableDataSource(this.pagedReviews);
    this.refreshPage();

    this.breakpointObserver.observe([Breakpoints.Handset])
    .pipe(
      map(result => result.matches)
    )
    .subscribe(matches => {
      this.isMobileView = matches;
    });
  }

  logout() {
    // Remove the login status from local storage
    localStorage.removeItem('loginStatus');
    localStorage.removeItem('clientId');

    // Redirect to the login page or another destination
    this.router.navigate(['/login']);
  }

 ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.responseHandlerService.setActionOngoing(false);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
  }

  login() {
    const storedFirstName = localStorage.getItem('firstName'); // Retrieve the first name from local storage
    if (storedFirstName) {
      this.loggedInUser = { firstName: storedFirstName }; // Store the first name in the loggedInUser variable
    // Remove the previous name from local storage
    } else {
      // Handle the case where the first name is not found in local storage
      localStorage.removeItem('firstName'); 
    }
  }

  checkScreenSize() {
    const screenWidth = window.innerWidth;
    this.isMobileView = screenWidth <= 320; // Adjust the width as per your requirements
  }

  downloadImages(review: Review) {
    // Log the review for debugging purposes
    console.log(review);
  
    const fileNames = review.reviewImage.split(',').map((fileName) => fileName.trim());
  
    fileNames.forEach((fileName) => {
      // Construct the image URL using the fileName
      const imageUrl = `http://localhost:8080/reviews/${fileName}`;
  
      // Use Angular's HttpClient to make an HTTP GET request to the image URL
      this.http.get(imageUrl, { responseType: 'blob' }).subscribe((data) => {
        // Create a blob object and trigger a file download
        const blob = new Blob([data], { type: 'image/jpeg' });
        const url = window.URL.createObjectURL(blob);
  
        // Create a link element for downloading the image
        const a = document.createElement('a');
        a.href = url;
  
        // Set the download attribute to the file name
        a.download = fileName;
  
        // Trigger a click event on the link element to initiate the download
        a.click();
  
        // Clean up
        window.URL.revokeObjectURL(url);
      });
    });
  }
  
  
  

  // Loads all the reviews using the ReviewApiService and updates the paged reviews
  loadReviews(): void {
    this.apiService.getAllReviews().subscribe((data: Review[]) => {
      this.reviews = data;
      this.updatePagedReviews();
      this.dataSource.data = this.pagedReviews; // Update the data source with pagedReviews
    });
  }  

   // Refreshes the page by loading reviews and updating paged reviews
  refreshPage(): void {
    this.loadReviews();
    this.updatePagedReviews();
  }

  // Updates the paged reviews based on the selected page size
  updatePagedReviews(): void {
    const startIndex = 0; // Define the start index for pagination
    this.pagedReviews = this.reviews.slice(startIndex, startIndex + this.selectedPageSize);
  }

  // Handles the change in page size and updates paged reviews accordingly
  onPageSizeChange(): void {
    this.updatePagedReviews();
  }

  onPageChange(event: PageEvent): void {
    const startIndex = event.pageIndex * event.pageSize;
    const endIndex = startIndex + event.pageSize;
    this.pagedReviews = this.reviews.slice(startIndex, endIndex);
  }

  // Filters reviews based on the selected product or refreshes the page if the selection is empty
  onProductFilterChange(): void {
    if (this.selectedProduct !== '') {
      this.pagedReviews = this.reviews.filter(
        (review) => review.productName === this.selectedProduct
      );
    } else {
      this.refreshPage(); // Refresh the page to show all reviews
    }
    this.paginator.firstPage(); // Reset the paginator to the first page
  }

  // Filters reviews based on the entered rate code or refreshes the page if the search input is empty
  onRateCodeSearch(): void {
    if (this.rateCodeSearch !== '') {
      this.pagedReviews = this.reviews.filter(review => review.rateCode.toString() === this.rateCodeSearch);
    } else {
      this.refreshPage();
    }
  }

   // Filters reviews based on the entered review ID or refreshes the page if the search input is empty
  onReviewIdSearch(): void {
    if (this.reviewIdSearch !== '') {
      this.pagedReviews = this.pagedReviews.filter(review => review.reviewId.toString() === this.reviewIdSearch);
    } else {
      this.refreshPage();
    }
  }
  
  // Toggles the visibility of sorting options for the reviews
  toggleSortingOptions(): void {
    this.showSortingOptions = !this.showSortingOptions;
  }


}
