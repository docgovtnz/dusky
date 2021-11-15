import { Directive, AfterViewInit, ElementRef } from '@angular/core';

@Directive({
  selector: 'input[appAutofocus], select[appAutofocus], textarea[appAutofocus]',
})
export class AutofocusDirective implements AfterViewInit {
  constructor(private el: ElementRef) {}

  ngAfterViewInit() {
    this.el.nativeElement.focus();
  }
}
