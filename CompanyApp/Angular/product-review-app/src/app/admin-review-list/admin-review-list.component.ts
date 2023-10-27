import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { Component, OnInit, ViewChild, HostListener } from "@angular/core";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { map } from "rxjs";
import { Review } from "../interfaces/Review";
import { ResponseHandlerService } from "../services/response-handler.service";
import { ReviewApiService } from "../services/review-api.service";
import { HttpErrorResponse } from "@angular/common/http";


@Component({
  selector: 'app-admin-review-list',
  templateUrl: './admin-review-list.component.html',
  styleUrls: ['./admin-review-list.component.scss'], 
  animations: []
})
export class AdminReviewListComponent implements OnInit {
  reviews: Review[] = [];
  pagedReviews: Review[] = [];
  selectedPageSize: number = 10;
  selectedProduct: string = '';
  rateCodeSearch: string = '';
  reviewIdSearch: string = '';
  showSortingOptions: boolean = false; // Flag to toggle sorting options
  selectedHeader: string = '';
  isAscending: any;
  desktopView: any;
  isMobileView: boolean = false;
  loggedInUser: any;
 
  
  @ViewChild(MatSort) sort !:MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource: any;
  username: string | undefined;
  jwtToken: string = '';

  constructor(
    private breakpointObserver: BreakpointObserver,
    private apiService: ReviewApiService, 
    public responseHandlerService: ResponseHandlerService,

  ) {}


  displayedColumns: string[] = ['reviewId', 'productName', 'firstName', 'lastName', 'zipCode', 'rateCode', 'comments', 'dateTime','delete'];

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

 ngAfterViewInit(): void {
    this.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.responseHandlerService.setActionOngoing(false);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
  }

  // Delete a review by reviewId
  deleteReview(review: any) {
    this.apiService.deleteReview(review).subscribe(
      (resp) => {
        this.refreshPage();
      },
      (error:HttpErrorResponse) => {
        console.log(error);
      }
    );
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

  // Loads all the reviews using the ReviewApiService and updates the paged reviews
  loadReviews(): void {
    this.apiService.getAllReviews().subscribe((data: Review[]) => {
      this.reviews = data;
      this.updatePagedReviews();
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
      this.pagedReviews = this.reviews.filter(review => review.productName === this.selectedProduct);
    } else {
      this.refreshPage();
    }
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
      this.pagedReviews = this.reviews.filter(review => review.reviewId.toString() === this.reviewIdSearch);
    } else {
      this.refreshPage();
    }
  }
  
  // Toggles the visibility of sorting options for the reviews
  toggleSortingOptions(): void {
    this.showSortingOptions = !this.showSortingOptions;
  }


}
