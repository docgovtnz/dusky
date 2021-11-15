import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-comments-edit',
  template: `
    <app-controlblock title="Comments" [noOffset]="true">
      <div class="form-group">
        <label for="comments">Comments</label>
        <textarea
          class="form-control"
          rows="10"
          id="comments"
          [(ngModel)]="entity[field]"
        ></textarea>
      </div>
    </app-controlblock>
  `,
})
export class CommentsEditComponent implements OnInit {
  @Input()
  entity: any;

  @Input()
  field: string;

  constructor() {}

  ngOnInit(): void {}
}
