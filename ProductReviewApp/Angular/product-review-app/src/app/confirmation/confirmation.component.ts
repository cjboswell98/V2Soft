import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.scss']
})
export class ConfirmationComponent implements OnInit {
  private countdown: number = 4; // Initial countdown time in seconds
  private countdownInterval: any; // Interval ID for the countdown timer

  constructor(private router: Router) { }

  ngOnInit() {
    // Initialize the countdown timer
    this.startCountdown();
  }

  private startCountdown() {
    const countdownElement = document.getElementById('countdown');
    if (countdownElement) {
      this.countdownInterval = setInterval(() => {
        if (this.countdown === 0) {
          clearInterval(this.countdownInterval);
          this.router.navigate(['/review-list']); // Redirect when countdown reaches 0
        } else {
          this.countdown--;
          // Update the countdown displayed on the page
          countdownElement.textContent = this.countdown.toString();
        }
      }, 1000); // Update the countdown every 1 second
    }
  }
}
