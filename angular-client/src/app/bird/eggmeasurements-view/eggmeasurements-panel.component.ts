import { Component, Input, OnInit } from '@angular/core';

import { EggMeasurementsEntity } from '../../domain/eggmeasurements.entity';
import { SettingService } from '../../setting/setting.service';

@Component({
  selector: 'app-eggmeasurements-panel',
  templateUrl: 'eggmeasurements-panel.component.html',
})
export class EggMeasurementsPanelComponent implements OnInit {
  @Input()
  eggmeasurementsRevision: EggMeasurementsEntity;
  defaultCoefficient: number = null;

  constructor(private settingService: SettingService) {}

  ngOnInit(): void {
    this.settingService
      .get(['DEFAULT_FRESH_WEIGHT_COEFFICIENT'])
      .subscribe(
        (res) =>
          (this.defaultCoefficient = res['DEFAULT_FRESH_WEIGHT_COEFFICIENT'])
      );
  }
  get calculatedWeight(): number {
    const measures = this.eggmeasurementsRevision;
    const defaultCoeff = this.defaultCoefficient;
    if (
      measures.eggWidth &&
      measures.eggLength &&
      (measures.fwCoefficientX104 || defaultCoeff)
    ) {
      return (
        ((measures.fwCoefficientX104 || defaultCoeff) / 10000) *
        (measures.eggLength * measures.eggWidth * measures.eggWidth)
      );
    }
    return -1;
  }
}
