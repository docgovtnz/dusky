/* eslint-disable @angular-eslint/directive-selector, @angular-eslint/no-host-metadata-property */

import { Directive, Input } from '@angular/core';

/**
 * This is a utility to make handling clicking on links better. In Angular 2 if a link <a> includes the href attribute
 * then Angular will attempt to route to the URL contained. If you want to write your own click handler for the link
 * you need something like this to stop angular navigating away from the page towards the URL in the href. It doesn't
 * seem to interfere with other wanted behaviours.
 *
 * See: https://stackoverflow.com/questions/43275297/angular-2-handle-anchor-links-with-href
 */
@Directive({
  selector: '[href]',
  host: {
    '(click)': 'doNothing($event)',
  },
})
export class MyLinkDirective {
  @Input() href: string;

  doNothing(event) {
    if (this.href.length === 0 || this.href === '#') {
      event.preventDefault();
    }
  }
}
