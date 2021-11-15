import { Subject } from 'rxjs/index';

import { Injectable } from '@angular/core';

import { Error } from './error';

@Injectable()
export class ErrorsService {
  private errors = new Subject<Error>();
  public errors$ = this.errors.asObservable();

  public fireError(errorMessage: string, errorDetails: string) {
    const error = new Error();
    error.errorMessage = errorMessage;
    error.errorDetails = errorDetails;
    this.errors.next(error);
  }
}
