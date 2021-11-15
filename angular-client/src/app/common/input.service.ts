import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * A service to help components communicate with each other such as the recordEdit component and the
 * observerEdit component.
 */
@Injectable()
export class InputService {
  /*Create Subject object.*/
  private clearInputRequest = new Subject<string>();
  private recordTypeRequest = new Subject<string>();

  /*Make it observable.*/
  clearInputRequest$ = this.clearInputRequest.asObservable();
  recordTypeRequest$ = this.recordTypeRequest.asObservable();

  /*Create method to pass data.*/
  clearInput(request: string) {
    this.clearInputRequest.next(request);
  }

  recordTypeSelected(request: string) {
    this.recordTypeRequest.next(request);
  }
}
