import { Router } from '@angular/router';
import { AbstractCriteria } from '../domain/criteria/abstract-criteria';
import { Location } from '@angular/common';

/**
 * This is the super class for all search components in the application and provides helper methods common to most search components.
 */
export class AbstractSearchComponent {
  constructor(private location: Location) {}

  /**
   * Changes the url relative to the applications url to match the supplied baseUrl and criteria.
   */
  protected goIfChanged(baseUrl: string, criteria: AbstractCriteria) {
    // convert the saved criteria to a url so we can set this in the brower
    const url = criteria.toUrl(baseUrl);
    // set the url if it is different to the current url only so we don't add an extra redundant history item
    if (!this.location.isCurrentPathEqualTo(url)) {
      this.location.go(url);
    }
  }

  protected navigate(router: Router, url: string, newTab: boolean) {
    if (newTab) {
      window.open('app/' + url, '_blank');
    } else {
      router.navigate([url]);
    }
  }
}
