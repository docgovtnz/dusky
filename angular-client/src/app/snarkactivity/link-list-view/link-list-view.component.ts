import { Component, Input, OnInit } from '@angular/core';

/**
 * Can be used for a list of simple router links. There is an expected naming convention of using {name} and then appending ID or Name to get the
 * Id and name label. There is also an expected object structure of ["id": <some id>, "name": <some name>, ...]
 *
 * => routerLink = '/{name}/{item.id}' and label = "{item.name}"
 *
 * DisplayProperty: <Property name="birds" label="Birds" viewType="LinkListView" editorCfg="bird"/>
 * ResultsView or other template: <app-link-list-view [linkUrlBase]="'/${dp.editorCfg}/'" [list]="item"></app-link-list-view>
 * Resulting Html: <app-link-list-view [linkUrlBase]="'/bird/'" [list]="item"></app-link-view>
 *
 */
@Component({
  selector: 'app-link-list-view',
  templateUrl: './link-list-view.component.html',
})
export class LinkListViewComponent implements OnInit {
  @Input()
  linkUrlBase: string;

  @Input()
  list: any[];

  constructor() {}

  ngOnInit() {}
}
