import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RevisionList } from '../domain/revision-list';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class RevisionService {
  constructor(private http: HttpClient) {}

  findRevisionList(entityId: string): Observable<RevisionList> {
    return this.http.get<RevisionList>('/api/revision/' + entityId);
  }
}
