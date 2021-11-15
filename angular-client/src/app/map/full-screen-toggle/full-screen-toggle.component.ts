import { Component, Input, OnInit } from '@angular/core';
import fscreen from 'fscreen';

@Component({
  selector: 'app-full-screen-toggle',
  templateUrl: './full-screen-toggle.component.html',
})
export class FullScreenToggleComponent implements OnInit {
  @Input()
  element: Element;

  constructor() {}

  ngOnInit() {}

  onFullScreenToggle() {
    fscreen.requestFullscreen(this.element);
  }
}
