import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export abstract class OptionListBaseService {
  constructor(protected http: HttpClient) {}

  public save(newList: any): Observable<any> {
    return this.http.post<any>('/api/options/optionLists/', newList);
  }
}
