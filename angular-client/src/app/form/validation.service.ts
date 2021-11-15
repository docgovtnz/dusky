import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { publishReplay, refCount, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ValidationService {
  // This creates one copy of the http response no matter how many times it's called. See: unmarked answer further down
  // in the following: https://stackoverflow.com/questions/49797910/angular-5-caching-http-service-api-calls
  readonly messageTemplateMap$: any = this.http
    .get('/api/validation/messageTemplateMap')
    .pipe(publishReplay(1), refCount(), take(1));

  constructor(private http: HttpClient) {}

  getDataValidationClass(entityClassName: string): Observable<any> {
    return this.http.get('/api/validation/' + entityClassName);
  }
}
