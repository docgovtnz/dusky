import { Component, OnInit, ViewChild } from '@angular/core';
import { SettingService } from './setting.service';
import { ValidationMessage } from '../domain/response/validation-message';
import { ReplicationService } from '../replication/replication.service';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { OptionsService } from '../common/options.service';
import { take } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
})
export class SettingsComponent implements OnInit {
  settings: any = {};
  messages: ValidationMessage[];
  serverMode: string;

  @ViewChild('refreshCacheModal', { static: true })
  public refreshCacheModal: ModalDirective;

  constructor(
    private settingService: SettingService,
    private replicationService: ReplicationService,
    private optionsService: OptionsService
  ) {}

  ngOnInit() {
    this.settingService
      .get([
        'AGE_CLASS_JUVENILE_THRESHOLD_DAYS',
        'AGE_CLASS_ADULT_THRESHOLD_YEARS',
        'MATING_QUALITY_MEDIUM_THRESHOLD',
        'MATING_QUALITY_HIGH_THRESHOLD',
        'WEIGHT_GRAPH_PC_FROM_MEAN_HIGH',
        'WEIGHT_GRAPH_PC_FROM_MEAN_LOW',
        'WEIGHT_DELTA_ROLLING_MIN_WINDOW_HRS',
        'DEFAULT_FRESH_WEIGHT_COEFFICIENT',
        'HELP',
      ])
      .subscribe((settings) => (this.settings = settings));

    this.replicationService.getSyncGatewayStatus().subscribe((status) => {
      this.serverMode = status.serverMode;
    });
  }

  onSave(): void {
    this.messages = null;
    this.settingService.put(this.settings).subscribe((response) => {
      this.settings = response.model;
      this.messages = response.messages;
    });
  }

  cacheRefreshMessage: string[] = [];
  max: number;
  done = 0;

  refreshCache(): void {
    this.cacheRefreshMessage = [];
    this.refreshCacheModal.show();
    const x = this.optionsService.getCaches();

    this.done = 0;
    this.max = x.length;

    for (let i = 0; i < x.length - 1; i++) {
      x[i].$refresh.pipe(take(1)).subscribe((c) => {
        this.cacheRefreshMessage.push('Refreshed: ' + c.cacheName);
        this.done++;
        x[i + 1].forceRefresh();
      });
    }
    x[x.length - 1].$refresh.pipe(take(1)).subscribe(() => {
      this.cacheRefreshMessage.push('Refreshed: ' + x[x.length - 1].cacheName);
      this.done++;
    });
    x[0].forceRefresh();
  }
}
