import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.scss'],
})
export class BannerComponent implements OnInit {
  @Input()
  title: string;

  @Input()
  subtitle: string;

  @Input()
  noOffset = false;

  @Input()
  withSpacer = false;

  constructor() {}

  ngOnInit(): void {}
}
