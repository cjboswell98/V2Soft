import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminReviewListComponent } from './admin-review-list.component';

describe('AdminReviewListComponent', () => {
  let component: AdminReviewListComponent;
  let fixture: ComponentFixture<AdminReviewListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminReviewListComponent]
    });
    fixture = TestBed.createComponent(AdminReviewListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
