
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
  isAscending: any;
  desktopView: any;
  isMobileView: boolean = false;
  
  @ViewChild(MatSort) sort !:MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  dataSource: any;

  constructor(
    private breakpointObserver: BreakpointObserver,
    private apiService: ReviewApiService, 
    public responseHandlerService: ResponseHandlerService
  ) {}


  displayedColumns: string[] = ['reviewId', 'productName', 'firstName', 'lastName', 'zipCode', 'rateCode', 'comments', 'dateTime'];

  // Initializes the component and refreshes the page
  ngOnInit(): void {
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
  async performAsyncAction(): Promise<void> {
    // Disable all functions when the action starts
    if (this.responseHandlerService.isActionOngoing()) {
      return;
    }
  
    try {
      // Set the action state to true to disable the button
      this.responseHandlerService.setActionOngoing(true);
  
      const result = await this.responseHandlerService.handleResponse(this.yourAsyncFunction());
      // Handle the result as needed
  
    } catch (error) {
      // Handle errors if any
      console.error('An error occurred:', error);
  
    } finally {
      // Set the action state to false to re-enable the button
      this.responseHandlerService.setActionOngoing(false);
    }
  }

  // Replace this with your actual asynchronous function
  private yourAsyncFunction(): Promise<any> {
    // Simulating an asynchronous action
    return new Promise(resolve => {
      setTimeout(() => {
        resolve('Action done');
      }, 2000);
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
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

  // Handles sorting of reviews based on the selected header key and the sorting order
//   sort(key: keyof Review): void {
//     const isAscending = this.selectedHeader === key ? !this.isAscending : true; // Toggle the sorting order if the same header is clicked again
//     this.selectedHeader = key;
  
//     this.pagedReviews.sort((a, b) => {
//       let comparison = 0;
  
//       if (key === 'reviewId') {
//         const valueA = parseInt(a[key] as string);
//         const valueB = parseInt(b[key] as string);
  
//         comparison = valueA - valueB;
//       } else {
//         const valueA = a[key] as string | Date;
//         const valueB = b[key] as string | Date;

//         if (valueA > valueB) {
//           comparison = 1;
//         } else if (valueA < valueB) {
//           comparison = -1;
//         }
//       }
  
//       return isAscending ? comparison : -comparison; // Inverting the comparison for descending order
//     });
  
//     this.isAscending = isAscending; // Store the current sorting order
// }

  
}
