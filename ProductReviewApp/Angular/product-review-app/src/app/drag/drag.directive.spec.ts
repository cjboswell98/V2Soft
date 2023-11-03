import { DragDirective } from './drag.directive';
import { DomSanitizer } from '@angular/platform-browser';

describe('DragDirective', () => {
  it('should create an instance', () => {
    const sanitizer = {} as DomSanitizer; // Mock the DomSanitizer
    const directive = new DragDirective(sanitizer);
    expect(directive).toBeTruthy();
  });
});
