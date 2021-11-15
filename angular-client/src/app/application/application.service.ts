import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationStatus } from './application-status';

@Injectable()
export class ApplicationService {
  constructor(private http: HttpClient) {}

  public getStatus(): Observable<ApplicationStatus> {
    return this.http.get<ApplicationStatus>('/api/application/status/');
  }
}
