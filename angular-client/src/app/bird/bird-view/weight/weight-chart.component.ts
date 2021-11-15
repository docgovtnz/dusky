import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { WeightService } from './weight.service';
import { WeightSummaryDto } from './weight-summary-dto';
import { Chart } from 'chart.js';
import * as moment from 'moment';
import { FormControl, FormGroup } from '@angular/forms';
import { combineLatest, merge } from 'rxjs';
import { WeightRefData } from './weight-ref-data';
import { Moment } from 'moment';
import { SettingService } from '../../../setting/setting.service';
import { debounceTime } from 'rxjs/operators';
import { ChartClickHandlerService } from './chart-click-handler.service';
import { DataSet } from './weight-chart-dataset';

const dateValidator = (minDate: Date) => (control: FormControl) => {
  const valid = !moment(control.value)
    .startOf('day')
    .isBefore(moment(minDate).startOf('day'));
  if (!valid) {
    return { minDate: 'From date must be after the hatch date.' };
  } else {
    return null;
  }
};

@Component({
  selector: 'app-weight-chart',
  templateUrl: 'weight-chart.component.html',
})
export class WeightChartComponent implements OnInit {
  private _birdID: string;

  searchForm = new FormGroup({
    dateRange: new FormControl(''),
    fromDate: new FormControl(''),
    toDate: new FormControl(''),
    graphType: new FormControl(),
    xAxisType: new FormControl(),
    strategy: new FormControl(),
  });

  weightList: WeightSummaryDto[];
  refData: WeightRefData;
  settings: any[];

  errorMessage: string;

  @Input()
  dateHatched: Date;

  @Input()
  dateDiscovered: Date;

  minDate: Moment;

  @Input()
  sex: string;

  dateRangeOptions: { value; label }[] = [
    { value: 'custom', label: 'Custom' },
    { value: '20', label: 'First 20 days' },
    { value: '50', label: 'First 50 days' },
  ];

  @ViewChild('lineCanvas') lineCanvas;
  lineChart: any;

  constructor(
    private weightService: WeightService,
    private settingService: SettingService,
    private chartClickHandlerService: ChartClickHandlerService
  ) {}

  @Input()
  set birdID(birdID: string) {
    if (this.lineChart) {
      // destroy the previous chart so that any mouse overs on previous data points don't pull back the line graph for the previous bird
      this.lineChart.destroy();
    }
    this._birdID = birdID;
    if (birdID) {
      this.loadWeightData();
    }
  }

  get birdID(): string {
    return this._birdID;
  }

  ngOnInit(): void {
    if (this.dateHatched) {
      this.searchForm.controls.fromDate.setValidators([
        dateValidator(this.dateHatched),
      ]);
    }

    this.minDate = this.dateHatched
      ? moment(this.dateHatched)
      : moment(this.dateDiscovered);

    if (!this.minDate || !this.minDate.isValid()) {
      this.errorMessage =
        'Cannot generate Weight Graph when neither "Date Hatched" nor "Discovery Date" are set.';
    }

    // Set defaults.
    this.searchForm.controls.dateRange.setValue('custom');
    this.searchForm.controls.fromDate.setValue(this.minDate);
    this.searchForm.controls.toDate.setValue(new Date());
    this.searchForm.controls.graphType.setValue('weight');
    this.searchForm.controls.xAxisType.setValue('date');
    this.searchForm.controls.strategy.setValue('raw');

    this.settingService
      .get(['AGE_CLASS_JUVENILE_THRESHOLD_DAYS'])
      .subscribe((v) => {
        const juvThreshold = Number(v['AGE_CLASS_JUVENILE_THRESHOLD_DAYS']);
        this.dateRangeOptions.push({
          value: juvThreshold,
          label: 'First ' + juvThreshold + ' days',
        });
      });

    this.searchForm.controls.dateRange.valueChanges.subscribe((dateRange) => {
      if (dateRange === 'custom') {
        // Enable the date controls.
        this.searchForm.controls.fromDate.enable();
        this.searchForm.controls.toDate.enable();
      } else {
        // Set the start date.
        this.searchForm.controls.fromDate.setValue(this.dateHatched);
        this.searchForm.controls.xAxisType.setValue('age');

        // Set the end data based on the range.
        const toDate = moment(this.dateHatched)
          .add(parseInt(this.searchForm.controls.dateRange.value, 10), 'day')
          .toDate();

        // Disable the date controls.
        this.searchForm.controls.toDate.setValue(toDate);
        this.searchForm.controls.fromDate.disable();
        this.searchForm.controls.toDate.disable();
      }
    });

    this.searchForm.controls.graphType.valueChanges.subscribe((next) => {
      this.searchForm.controls.strategy.setValue(
        next === 'weight' ? 'raw' : 'min'
      );
    });

    this.searchForm.controls.strategy.valueChanges.subscribe((next) => {
      this.loadWeightData();
    });

    merge(
      //this.searchForm.controls.dateRange.valueChanges - Don't listen since the change will affect the from/to dates.
      this.searchForm.controls.fromDate.valueChanges,
      this.searchForm.controls.toDate.valueChanges,
      // this.searchForm.controls.graphType.valueChanges, - ignore as this changes the graph model, which will trigger a reload.
      this.searchForm.controls.xAxisType.valueChanges
    )
      .pipe(debounceTime(100))
      .subscribe((next) => {
        if (
          this.searchForm.controls.fromDate.valid ||
          this.searchForm.controls.fromDate.disabled
        ) {
          console.log('Regenerating graph');
          this.createChart();
        } else {
          console.log('Not generating graph as the fromDate is invalid');
        }
      });
  }

  loadWeightData() {
    if (this.birdID) {
      const birdWeights = this.weightService.findByBirdID(
        this.birdID,
        null,
        null,
        this.searchForm.controls.strategy.value
      );
      const refData = this.weightService.getReferenceData();
      const settings = this.settingService.get([
        'WEIGHT_GRAPH_PC_FROM_MEAN_HIGH',
        'WEIGHT_GRAPH_PC_FROM_MEAN_LOW',
      ]);

      combineLatest(birdWeights, refData, settings).subscribe((vals) => {
        this.weightList = vals[0];
        this.refData = vals[1];
        this.settings = vals[2];
        this.createChart();
      });
    }
  }

  createChart() {
    if (this.lineChart) {
      this.lineChart.destroy();
    }

    if (this.searchForm.controls.graphType.value === 'weight') {
      this.createWeightChart();
    } else {
      this.createDeltaChart();
    }
  }

  createWeightChart() {
    const labels = [];

    const startDate: Moment = moment(
      this.searchForm.controls.fromDate.value
    ).startOf('day');
    const endDate: Moment = moment(this.searchForm.controls.toDate.value).endOf(
      'day'
    );
    const momentHatchDate: Moment = moment(this.dateHatched);
    const byAge = this.searchForm.controls.xAxisType.value === 'age';
    const totalDays = endDate.diff(startDate, 'day');
    const labelFreq =
      totalDays <= 31
        ? 'day'
        : totalDays <= 150
        ? 'week'
        : totalDays < 365 * 3
        ? 'month'
        : 'year';
    const midLinePercent = Number(
      this.settings['WEIGHT_GRAPH_PC_FROM_MEAN_HIGH']
    );
    const lowLinePercent = Number(
      this.settings['WEIGHT_GRAPH_PC_FROM_MEAN_LOW']
    );

    const dataSets: any[] = [];

    // Prepare the weight data.
    const weightData: { x; y; recordId; cropStatus }[] = [];
    this.weightList
      .filter(
        (value) =>
          !moment(value.dateTime).isBefore(startDate) &&
          !moment(value.dateTime).isAfter(endDate)
      )
      .forEach((value) => {
        const x = byAge ? value.ageDays : value.dateTime;
        const y = value.weight * 1000;
        const recordId = value.recordId;
        const cropStatus = value.cropStatus;
        weightData.push({
          x,
          y,
          recordId,
          cropStatus,
        });
      });

    // Add the weight data.
    dataSets.push({
      label: 'Bird Weight',
      datasetStroke: true,
      lineTension: 0.0,
      fill: false,
      backgroundColor: 'rgba(0,0,0,0.4)',
      borderColor: 'rgba(0,0,0,1)',
      borderCapStyle: 'butt',
      borderDash: [],
      borderDashOffset: 0.0,
      borderJoinStyle: 'miter',
      cubicInterpolationMode: 'monotone',
      pointBorderColor: 'rgba(0,0,0,1)',
      pointBackgroundColor: '#fff',
      pointBorderWidth: 1,
      pointHoverRadius: 5,
      pointHoverBackgroundColor: 'rgba(0,0,0,1)',
      pointHoverBorderColor: 'rgba(0,0,0,1)',
      pointHoverBorderWidth: 2,
      pointRadius: 4,
      pointHitRadius: 10,
      data: weightData,
      spanGaps: true,
      showLine: true,
    });

    // Reference data is only valid if the hatch date is known.
    if (this.dateHatched) {
      const minAge = startDate.diff(momentHatchDate, 'hour') / 24;
      const maxAge = endDate.diff(momentHatchDate, 'hour') / 24;

      // Add the female reference data.
      if (this.sex !== 'Male') {
        const refData: { x; y }[] = [];
        this.refData.femaleReferenceData
          .filter((value) => value.x >= minAge && value.x <= maxAge)
          .forEach((value) => {
            refData.push({
              x: byAge
                ? value.x
                : momentHatchDate.clone().add(value.x * 24, 'hour'),
              y: value.y * 1000,
            });
          });

        dataSets.push(
          new DataSet('Female Mean', refData, 'rgba(255,51,153,1)', [])
        );
        dataSets.push(
          new DataSet(
            'Female Mean (' +
              (midLinePercent > 0 ? '+' : '') +
              midLinePercent +
              '%)',
            this.createPercentileDataSet(refData, (100 + midLinePercent) / 100),
            'rgba(255,51,153,1)',
            [5, 5]
          )
        );
        dataSets.push(
          new DataSet(
            'Female Mean (' +
              (lowLinePercent > 0 ? '+' : '') +
              lowLinePercent +
              '%)',
            this.createPercentileDataSet(refData, (100 + lowLinePercent) / 100),
            'rgba(255,51,153,1)',
            [2, 10]
          )
        );
      }

      if (this.sex !== 'Female') {
        const refData: { x; y }[] = [];
        this.refData.maleReferenceData
          .filter((value) => value.x >= minAge && value.x <= maxAge)
          .forEach((value) => {
            refData.push({
              x: byAge
                ? value.x
                : momentHatchDate.clone().add(value.x * 24, 'hour'),
              y: value.y * 1000,
            });
          });

        dataSets.push(
          new DataSet('Male Mean', refData, 'rgba(73,185,255,1)', [])
        );
        dataSets.push(
          new DataSet(
            'Male Mean (' +
              (midLinePercent > 0 ? '+' : '') +
              midLinePercent +
              '%)',
            this.createPercentileDataSet(refData, (100 + midLinePercent) / 100),
            'rgba(73,185,255,1)',
            [5, 5]
          )
        );
        dataSets.push(
          new DataSet(
            'Male Mean (' +
              (lowLinePercent > 0 ? '+' : '') +
              lowLinePercent +
              '%)',
            this.createPercentileDataSet(refData, (100 + lowLinePercent) / 100),
            'rgba(73,185,255,1)',
            [2, 10]
          )
        );
      }
    }

    this.lineChart = new Chart(this.lineCanvas.nativeElement, {
      type: 'scatter',
      data: {
        labels,
        datasets: dataSets,
      },
      options: {
        events: ['click', 'mousemove', 'mouseout'],
        onClick: (evt, item) => {
          this.chartClickHandlerService.onClick(evt, item, this.lineChart);
        },
        onHover: (evt, item) => {
          this.chartClickHandlerService.chartHoverHandler(evt, item);
        },
        scales: {
          yAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: 'Weight (grams)',
              },
              ticks: {},
            },
          ],
          xAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: byAge ? 'Age (days)' : 'Date',
              },
              type: byAge ? 'linear' : 'time',
              time: {
                unit: labelFreq,
              },
            },
          ],
        },
        tooltips: {
          callbacks: {
            label: (tooltipItem, data) => {
              let label = data.datasets[tooltipItem.datasetIndex].label || '';

              if (label) {
                label += ': ';
              }

              const cropStatus =
                data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index]
                  .cropStatus;
              if (cropStatus) {
                label += '(Crop: ' + cropStatus + ') ';
              }

              if (isNaN(tooltipItem.xLabel)) {
                // TODO h is not in 24 time - does that matter?
                label +=
                  moment(new Date(tooltipItem.xLabel)).format(
                    'DD/MM/YY HH:mm'
                  ) + ', ';
              } else {
                label += Math.round(tooltipItem.xLabel * 100) / 100;
                label += ' days, ';
              }

              label += this.weightService.formatWeightFromGrams(
                tooltipItem.yLabel
              );
              return label;
            },
          },
        },
      },
    });
  }

  createDeltaChart() {
    const labels = [];
    const data = [];

    const startDate: Moment = moment(
      this.searchForm.controls.fromDate.value
    ).startOf('day');
    const endDate: Moment = moment(this.searchForm.controls.toDate.value).endOf(
      'day'
    );
    const momentHatchDate: Moment = moment(this.dateHatched).startOf('day');
    const byAge = this.searchForm.controls.xAxisType.value === 'age';
    const totalDays = endDate.diff(startDate, 'day');
    const labelFreq =
      totalDays <= 31
        ? 'day'
        : totalDays <= 150
        ? 'week'
        : totalDays < 365 * 3
        ? 'month'
        : 'year';

    const dataSets: any[] = [];

    // Prepare the weight data.
    const weightData: { x; y }[] = [];
    this.weightList
      .filter(
        (value) =>
          !moment(value.dateTime).isBefore(startDate) &&
          !moment(value.dateTime).isAfter(endDate)
      )
      .forEach((value) =>
        weightData.push({
          x: byAge ? value.ageDays : value.dateTime,
          y: value.dailyWeightChange,
        })
      );

    // Add the weight data.
    dataSets.push({
      label: 'Bird Weight (kg)',
      datasetStroke: true,
      lineTension: 0.0,
      fill: false,
      backgroundColor: 'rgba(0,0,0,0.4)',
      borderColor: 'rgba(0,0,0,1)',
      borderCapStyle: 'butt',
      borderDash: [],
      borderDashOffset: 0.0,
      borderJoinStyle: 'miter',
      cubicInterpolationMode: 'monotone',
      pointBorderColor: 'rgba(0,0,0,1)',
      pointBackgroundColor: '#fff',
      pointBorderWidth: 1,
      pointHoverRadius: 5,
      pointHoverBackgroundColor: 'rgba(0,0,0,1)',
      pointHoverBorderColor: 'rgba(0,0,0,1)',
      pointHoverBorderWidth: 2,
      pointRadius: 4,
      pointHitRadius: 10,
      data: weightData,
      spanGaps: true,
      showLine: true,
    });

    // Reference data is only valid if the hatch date is known.
    if (this.dateHatched) {
      const minAge = startDate.diff(moment(this.dateHatched), 'hour') / 24;
      const maxAge = endDate.diff(moment(this.dateHatched), 'hour') / 24;

      // Add the female reference data.
      if (this.sex === 'Female') {
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.femaleDelta90pc,
            minAge,
            maxAge,
            byAge,
            'Female 90%',
            'rgba(119,119,119,1)',
            [10, 1],
            2,
            'rgba(255,0,128,1)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.femaleDelta80pc,
            minAge,
            maxAge,
            byAge,
            'Female 80%',
            'rgba(119,119,119,1)',
            [5, 2],
            3,
            'rgba(255,0,128,0.7)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.femaleDelta50pc,
            minAge,
            maxAge,
            byAge,
            'Female 50%',
            'rgba(119,119,119,1)',
            [],
            4,
            'rgba(255,0,128,0.4)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.femaleDelta20pc,
            minAge,
            maxAge,
            byAge,
            'Female 20%',
            'rgba(119,119,119,1)',
            [2, 5],
            5,
            'rgba(255,0,128,0.1)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.femaleDelta10pc,
            minAge,
            maxAge,
            byAge,
            'Female 10%',
            'rgba(119,119,119,1)',
            [1, 5],
            false,
            'rgba(255,255,255,1)'
          )
        );
      } else if (this.sex === 'Male') {
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.maleDelta90pc,
            minAge,
            maxAge,
            byAge,
            'Male 90%',
            'rgba(119,119,119,1)',
            [10, 1],
            2,
            'rgba(73,185,255,1)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.maleDelta80pc,
            minAge,
            maxAge,
            byAge,
            'Male 80%',
            'rgba(119,119,119,1)',
            [5, 2],
            3,
            'rgba(73,185,255,0.7)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.maleDelta50pc,
            minAge,
            maxAge,
            byAge,
            'Male 50%',
            'rgba(119,119,119,1)',
            [],
            4,
            'rgba(73,185,255,0.4)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.maleDelta20pc,
            minAge,
            maxAge,
            byAge,
            'Male 20%',
            'rgba(119,119,119,1)',
            [2, 5],
            5,
            'rgba(73,185,255,0.1)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.refData.maleDelta10pc,
            minAge,
            maxAge,
            byAge,
            'Male 10%',
            'rgba(119,119,119,1)',
            [1, 5],
            false,
            'rgba(255,255,255,1)'
          )
        );
      } else {
        dataSets.push(
          this.createDeltaDataSet(
            this.avgDataSource(
              this.refData.maleDelta90pc,
              this.refData.femaleDelta90pc
            ),
            minAge,
            maxAge,
            byAge,
            'Avg 90%',
            'rgba(119,119,119,1)',
            [10, 1],
            2,
            'rgba(255,255,0,1)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.avgDataSource(
              this.refData.maleDelta80pc,
              this.refData.femaleDelta80pc
            ),
            minAge,
            maxAge,
            byAge,
            'Avg 80%',
            'rgba(119,119,119,1)',
            [5, 2],
            3,
            'rgba(255,255,0,0.7)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.avgDataSource(
              this.refData.maleDelta50pc,
              this.refData.femaleDelta50pc
            ),
            minAge,
            maxAge,
            byAge,
            'Avg 50%',
            'rgba(119,119,119,1)',
            [],
            4,
            'rgba(255,255,0,0.4)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.avgDataSource(
              this.refData.maleDelta20pc,
              this.refData.femaleDelta20pc
            ),
            minAge,
            maxAge,
            byAge,
            'Avg 20%',
            'rgba(119,119,119,1)',
            [2, 5],
            5,
            'rgba(255,255,0,0.1)'
          )
        );
        dataSets.push(
          this.createDeltaDataSet(
            this.avgDataSource(
              this.refData.maleDelta10pc,
              this.refData.femaleDelta10pc
            ),
            minAge,
            maxAge,
            byAge,
            'Avg 10%',
            'rgba(119,119,119,1)',
            [1, 5],
            false,
            'rgba(255,255,0,1)'
          )
        );
      }
    }

    this.lineChart = new Chart(this.lineCanvas.nativeElement, {
      type: 'scatter',
      data: {
        labels,
        datasets: dataSets,
      },
      options: {
        scales: {
          yAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: 'Daily Weight Change (%)',
              },
            },
          ],
          xAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: byAge ? 'Age (days)' : 'Date',
              },
              type: byAge ? 'linear' : 'time',
              time: {
                unit: labelFreq,
              },
            },
          ],
        },
      },
    });
    this.lineChart.generateLegend();
  }

  createDeltaDataSet(
    dataSource: { x; y }[],
    minAge: number,
    maxAge: number,
    byAge: boolean,
    label: string,
    color: string,
    dashes: number[],
    fill: any,
    fillColor: string
  ): DataSet {
    const refData: { x; y }[] = [];
    dataSource
      .filter((value) => value.x >= minAge && value.x <= maxAge)
      .forEach((value) => {
        refData.push({
          x: byAge
            ? value.x
            : moment(this.dateHatched).add(value.x * 24, 'hour'),
          y: value.y,
        });
      });

    return new DataSet(label, refData, color, dashes, fill, fillColor);
  }

  avgDataSource(dataSource: { x; y }[], dataSource2: { x; y }[]): { x; y }[] {
    const byAge: number[] = [];
    const result: { x; y }[] = [];
    dataSource.forEach((v) => (byAge[v.x] = v.y));
    dataSource2.forEach(
      (v) => (byAge[v.x] = byAge[v.x] ? (byAge[v.x] + v.y) / 2 : v.y)
    );

    byAge.forEach((key, val) => result.push({ x: val, y: key }));
    return result;
  }

  createPercentileDataSet(source: { x; y }[], percent: number): { x; y }[] {
    const result: { x; y }[] = [];
    source.forEach((v) => result.push({ x: v.x, y: v.y * percent }));
    return result;
  }
}
