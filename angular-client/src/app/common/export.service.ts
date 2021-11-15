import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';

import { User } from '../authentication/service/user';
import { AuthenticationService } from '../authentication/service/authentication.service';

@Injectable()
export class ExportService {
  private currentUser: User;

  constructor(private authenticationService: AuthenticationService) {
    this.authenticationService.currentUser$.subscribe(
      (currentUser) => (this.currentUser = currentUser)
    );
  }

  getExportUrlPath(basePath: string, criteria): string {
    let hp = new HttpParams();
    Object.keys(criteria).forEach((i) => {
      // pageNumber and pageSize are not relevant criteria for exporting
      if (i !== 'pageNumber' && i !== 'pageSize') {
        if (criteria[i] instanceof Date) {
          hp = hp.set(i, criteria[i].toISOString());
        } else if (criteria[i] !== null) {
          hp = hp.set(i, criteria[i]);
        }
      }
    });
    return `${basePath}?${hp.toString()}&jwsToken=${this.currentUser.token}`;
  }
}
