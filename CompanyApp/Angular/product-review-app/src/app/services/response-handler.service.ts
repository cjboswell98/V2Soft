import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ResponseHandlerService {

  private isActionInProgress: boolean = false;

  constructor() { }

  public async handleResponse<T>(promise: Promise<T>): Promise<T> {
    this.isActionInProgress = true;
    try {
      const result = await promise;
      return result;
    } finally {
      this.isActionInProgress = false;
    }
  }

  public isActionOngoing(): boolean {
    return this.isActionInProgress;
  }

  public setActionOngoing(value: boolean): void {
    this.isActionInProgress = value;
  }

}
