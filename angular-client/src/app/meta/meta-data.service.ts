import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MetaClass } from './domain/meta-class';

@Injectable({
  providedIn: 'root',
})
export class MetaDataService {
  constructor(private http: HttpClient) {}

  getMetaClass(entityClassName: string): Observable<MetaClass> {
    return this.http.get<MetaClass>('/api/meta/' + entityClassName);
  }
}
