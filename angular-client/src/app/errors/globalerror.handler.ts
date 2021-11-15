import { ErrorHandler, Injectable, Injector } from '@angular/core';

import { ErrorsService } from './errors.service';
@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private injector: Injector) {}

  private get errorService(): ErrorsService {
    return this.injector.get(ErrorsService);
  }

  handleError(error) {
    this.errorService.fireError('An error has occurred', error);

    // IMPORTANT: Rethrow the error otherwise it gets swallowed
    //throw error;
  }
}
