import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-comments-view',
  template: `
    <app-controlblock title="Comments" *ngIf="revision" [noOffset]="true">
      <dl class="data-group">
        <dt>Comments</dt>
        <dd class="comments-text">{{ revision ? revision[field] : '' }}</dd>
      </dl>
    </app-controlblock>
  `,
  styleUrls: ['./comments-view.component.scss'],
})
export class CommentsViewComponent implements OnInit {
  @Input()
  revision: any;

  @Input()
  field = 'comments';

  constructor() {}

  ngOnInit(): void {}
}
