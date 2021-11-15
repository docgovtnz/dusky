import { Component, Input, OnInit } from '@angular/core';

/**
 * Can be used for a simple router link. There is an expected naming convention of using {name} and then appending ID or Name to get the
 * Id and name label.
 *
 * => routerLink = '/{name}/{name}ID' and label = "item.{name}Name"
 *
 * DisplayProperty: <Property name="birdID" label="Bird" viewType="LinkView" editorCfg="bird"/>
 * ResultsView template: <app-link-view [linkUrl]="'/${dp.editorCfg}/' + item.${dp.editorCfg}ID" [label]="item.${dp.editorCfg}Name"></app-link-view>
 * Resulting Html: <app-link-view [linkUrl]="'/bird/' + item.birdID" [label]="item.birdName"></app-link-view>
 *
 */
@Component({
  selector: 'app-link-view',
  templateUrl: './link-view.component.html',
})
export class LinkViewComponent implements OnInit {
  @Input()
  linkUrl: string;

  @Input()
  label: string;

  constructor() {}

  ngOnInit() {}
}
