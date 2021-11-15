import { map } from 'rxjs/operators';
import { Observable } from 'rxjs/internal/Observable';

import { Component, Input, OnInit } from '@angular/core';

import { SettingService } from './../../setting/setting.service';

@Component({
  selector: 'app-checkmate-quality',
  templateUrl: './checkmate-quality.component.html',
})
export class CheckmateQualityComponent implements OnInit {
  @Input()
  quality: number;

  constructor(private settingService: SettingService) {}

  ngOnInit() {}

  deriveQuality(): Observable<string> {
    return this.settingService.getAll().pipe(
      map((all) => {
        if (this.quality === null) {
          return '';
        } else if (this.quality < all['MATING_QUALITY_MEDIUM_THRESHOLD']) {
          return 'Low';
        } else if (this.quality < all['MATING_QUALITY_HIGH_THRESHOLD']) {
          return 'Medium';
        } else {
          return 'High';
        }
      })
    );
  }
}
