import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SettingService } from '../../setting/setting.service';

import { OptionListBaseService } from './option-list-base.service';

@Injectable()
export class OptionListService extends OptionListBaseService {
  constructor(http: HttpClient, private settingService: SettingService) {
    super(http);
  }

  public isAuthorizedToAdd(): boolean {
    return this.settingService.isAuthorizedToAdd();
  }
}
