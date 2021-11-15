import { Observable } from 'rxjs';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { AuthenticationService } from '../authentication/service/authentication.service';
import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs/internal/operators';
import { ErrorsService } from './errors.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private injector: Injector) {}

  public get authenticationService(): AuthenticationService {
    return this.injector.get(AuthenticationService);
  }

  public get errorService(): ErrorsService {
    return this.injector.get(ErrorsService);
  }

  intercept(
    request: HttpRequest<any>,
    nextHandler: HttpHandler
  ): Observable<HttpEvent<any>> {
    return nextHandler.handle(request).pipe(
      tap(
        () => {},
        (error) => {
          if (error instanceof HttpErrorResponse) {
            switch (error.status) {
              case 401:
                {
                  const router = this.injector.get(Router);
                  console.log('HttpErrorInterceptor 401: ' + router.url);
                  const optionalMessage =
                    error.error &&
                    error.error.message &&
                    error.error.message.indexOf('JWT expired') > 0
                      ? 'Session expired, please login again.'
                      : null;
                  this.authenticationService.startLogin(
                    router.url,
                    optionalMessage
                  );
                }
                break;
              case 403:
                {
                  const router = this.injector.get(Router);
                  console.log('HttpErrorInterceptor 403: ' + router.url);
                  const optionalMessage =
                    error.error &&
                    error.error.message &&
                    error.error.message.indexOf('JWT expired') > 0
                      ? 'Not authorised, please login.'
                      : null;
                  this.authenticationService.startLogin(
                    router.url,
                    optionalMessage
                  );
                }

                break;
              case 404:
                console.log('HttpErrorInterceptor 404');
                let errDetails = 'Not found';
                if (error.error.message) {
                  if (typeof error.error.message === 'string') {
                    errDetails = error.error.message;
                  }
                }

                this.errorService.fireError(
                  'The document you are trying to view no longer exists',
                  errDetails
                );
                break;

              default:
                const serviceLevel = request.headers.get('x_service_level');
                if (serviceLevel && serviceLevel === 'optional') {
                  // error is a 504 or higher and we don't need to notify the user about this request.
                  // June-2019 removed the 504 condition to suppress errors during Upgrade process because
                  // SG status calls were getting browser could not connect errors while the server was restarting
                  // Optional means optional - so ignore notification of errors to users
                } else {
                  let msg;
                  if (error.error) {
                    msg =
                      'HttpErrorInterceptor got status: ' +
                      error.error.status +
                      ' ' +
                      error.error.message +
                      ' ' +
                      error.error.path;
                  } else {
                    msg =
                      'HttpErrorInterceptor got status: ' +
                      error.status +
                      ' ' +
                      error.statusText +
                      ' ' +
                      error.message;
                  }

                  console.log(msg);
                  this.errorService.fireError(
                    'There was an error communicating with the server.',
                    msg
                  );
                }
                break;
            }
          } else {
            console.log(
              'HttpErrorInterceptor - some other kind of error: ' +
                JSON.stringify(error)
            );
            this.errorService.fireError(
              'An unknown error occurred trying to communicate with the server',
              JSON.stringify(error)
            );
          }
        }
      )
    );
  }
}
