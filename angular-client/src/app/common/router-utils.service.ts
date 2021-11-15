import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class RouterUtilsService {
  constructor(private router: Router) {}

  navigateWithNewTab(url: string, newTab: boolean) {
    if (newTab) {
      window.open('app/' + url, '_blank');
    } else {
      this.router.navigate([url]);
    }
  }
}
