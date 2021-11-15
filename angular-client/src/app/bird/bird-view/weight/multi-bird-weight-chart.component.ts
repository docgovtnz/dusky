import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { WeightService } from './weight.service';
import { BirdService } from '../../bird.service';
import { SettingService } from '../../../setting/setting.service';
import { OptionsService } from '../../../common/options.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BirdCriteria } from '../../bird.criteria';
import { merge, Observable, Subscription, of } from 'rxjs';
import { Chart } from 'chart.js';
import * as ColorHash from 'color-hash';
import { FormControl, FormGroup } from '@angular/forms';
import { ChartClickHandlerService } from './chart-click-handler.service';
import { LegendItem } from './legend-item';
import * as moment from 'moment';
import { debounceTime } from 'rxjs/operators';

const fromDateValidator = (minDate: Date) => (control: FormControl) => {
  const dateTxt = minDate.toLocaleDateString();
  const valid = !moment(control.value)
    .startOf('day')
    .isBefore(moment(minDate).startOf('day'));
  if (!valid) {
    return { minDate: 'Must be after ' + dateTxt };
  } else {
    return null;
  }
};

const toDateValidator = (maxDate: Date) => (control: FormControl) => {
  const dateTxt = maxDate.toLocaleDateString();
  const valid = !moment(control.value)
    .startOf('day')
    .isAfter(moment(maxDate).startOf('day'));
  if (!valid) {
    return { maxDate: 'Must not be after ' + dateTxt };
  } else {
    return null;
  }
};

@Component({
  selector: 'app-multi-bird-weight-chart',
  templateUrl: 'multi-bird-weight-chart.component.html',
})
export class MultiBirdWeightChartComponent implements OnInit, OnDestroy {
  routeSubscription: Subscription;
  birdCriteria: BirdCriteria;
  colorHash = new ColorHash();

  // datasets: Dataset[];
  legendItems: LegendItem[];

  @ViewChild('multiBirdWeightCanvas', { static: true }) lineCanvas;
  lineChart: any;

  dateRangeOptions2: { value; label }[] = [
    { value: 'custom', label: 'Custom' },
    { value: '5', label: '5 days' },
    { value: '10', label: '10 days' },
    { value: '20', label: '20 days' },
    { value: '50', label: '50 days' },
    { value: '150', label: '150 days' },
    { value: '365', label: '365 days' },
  ];

  dateRangeMin = new Date('2099-01-01');
  dateRangeMax = new Date('1990-01-01');
  dateRangeTotal = 3650;
  dateRangeDataWeight: any;
  dateRangeDataWeightMin: any;
  dateRangeDataDelta: any;
  dateRangeDataDeltaMin: any;

  modelOptions: { value; label }[] = [
    { value: 'RAW', label: 'Raw' },
    { value: 'MIN', label: 'Min' },
  ];

  graphOptions: { value; label }[] = [
    { value: 'WEIGHT', label: 'Weight' },
    { value: 'DELTA', label: 'Delta' },
  ];

  searchForm = new FormGroup({
    dateRange: new FormControl(),
    fromDate: new FormControl(''),
    toDate: new FormControl(''),
    graphType: new FormControl(),
    modelType: new FormControl(),
  });

  constructor(
    private weightService: WeightService,
    private birdService: BirdService,
    private settingService: SettingService,
    private optionsService: OptionsService,
    private chartClickHandlerService: ChartClickHandlerService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Set the default.
    this.searchForm.controls.dateRange.setValue('20');
    this.searchForm.controls.graphType.setValue('WEIGHT');
    this.searchForm.controls.modelType.setValue('RAW');
    this.searchForm.controls.fromDate.disable();
    this.searchForm.controls.toDate.disable();
    this.searchForm.controls.dateRange.disable();

    this.searchForm.controls.dateRange.valueChanges.subscribe((dateRange) => {
      if (dateRange === 'custom') {
        // Enable the date controls.
        this.searchForm.controls.fromDate.setValue(this.dateRangeMin);
        this.searchForm.controls.toDate.setValue(this.dateRangeMax);
        this.dateRangeTotal =
          (this.searchForm.controls.toDate.value.getTime() -
            this.searchForm.controls.fromDate.value.getTime()) /
          (1000 * 60 * 60 * 24);
        this.searchForm.controls.fromDate.enable();
        this.searchForm.controls.toDate.enable();
      } else {
        this.searchForm.controls.fromDate.setValue(null);
        this.searchForm.controls.toDate.setValue(null);
        this.searchForm.controls.fromDate.disable();
        this.searchForm.controls.toDate.disable();
      }
      this.getData(this.birdCriteria);
    });

    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.birdCriteria = new BirdCriteria();
      // set defaults before populating from params
      this.birdCriteria.showAlive = true;
      this.birdCriteria.populateFromParams(params);

      //todo Retrieve chart data with date for date range calculation and cache
      this.weightService
        .getMultiBirdDataWithDate(this.birdCriteria, 'RAW')
        .subscribe((d) => {
          // console.log('## initial data with date');
          // console.log(d);
          this.dateRangeDataWeight = d;
          const dataItems = d.filter((li) => li.datasets);
          this.dateRangeDataWeight = dataItems;
          //todo each bird is an item
          for (let i = 0; i < dataItems.length; i++) {
            const ds = dataItems[i].datasets;
            for (let j = 0; j < ds.length; j++) {
              for (let k = 0; k < ds[j].dataDate.length; k++) {
                ds[j].dataDate[k].x = new Date(ds[j].dataDate[k].x);
                if (this.dateRangeMin > ds[j].dataDate[k].x) {
                  this.dateRangeMin = ds[j].dataDate[k].x;
                }
                if (this.dateRangeMax < ds[j].dataDate[k].x) {
                  this.dateRangeMax = ds[j].dataDate[k].x;
                }
              }
              ds[j].data = ds[j].dataDate;
            }
          }
          console.log('## done processing initial dates');
          // this.searchForm.controls.fromDate.setValue(this.dateRangeMin);
          // this.searchForm.controls.toDate.setValue(this.dateRangeMax);
          this.dateRangeTotal =
            (this.dateRangeMax.getTime() - this.dateRangeMin.getTime()) /
            (1000 * 60 * 60 * 24);
          if (this.dateRangeMin < this.dateRangeMax) {
            this.searchForm.controls.fromDate.setValidators([
              fromDateValidator(this.dateRangeMin),
              toDateValidator(this.dateRangeMax),
            ]);
            this.searchForm.controls.toDate.setValidators([
              fromDateValidator(this.dateRangeMin),
              toDateValidator(this.dateRangeMax),
            ]);
          }

          this.searchForm.controls.dateRange.enable();
        });

      this.weightService
        .getMultiBirdDeltaDataWithDate(this.birdCriteria, 'RAW')
        .subscribe((d) => {
          // console.log('Delta Data raw');
          // console.log(d);
          const dataItems = d.filter((li) => li.datasets);
          this.dateRangeDataDelta = dataItems;
          //todo each bird is an item
          for (let i = 0; i < dataItems.length; i++) {
            const ds = dataItems[i].datasets;
            for (let j = 0; j < ds.length; j++) {
              ds[j].data = ds[j].dataDate;
            }
          }
        });

      this.weightService
        .getMultiBirdDeltaDataWithDate(this.birdCriteria, 'MIN')
        .subscribe((d) => {
          // console.log('Delta Data Min');
          // console.log(d);
          const dataItems = d.filter((li) => li.datasets);
          this.dateRangeDataDeltaMin = dataItems;
          //todo each bird is an item
          for (let i = 0; i < dataItems.length; i++) {
            const ds = dataItems[i].datasets;
            for (let j = 0; j < ds.length; j++) {
              ds[j].data = ds[j].dataDate;
            }
          }
        });

      this.weightService
        .getMultiBirdDataWithDate(this.birdCriteria, 'MIN')
        .subscribe((d) => {
          // console.log('Weight Data MIN');
          // console.log(d);
          const dataItems = d.filter((li) => li.datasets);
          this.dateRangeDataWeightMin = dataItems;
          //todo each bird is an item
          for (let i = 0; i < dataItems.length; i++) {
            const ds = dataItems[i].datasets;
            for (let j = 0; j < ds.length; j++) {
              ds[j].data = ds[j].dataDate;
            }
          }
        });
      //to reset after chart type change  *****
      this.searchForm.controls.modelType.valueChanges.subscribe(() =>
        this.getData(this.birdCriteria)
      );
      this.searchForm.controls.graphType.valueChanges.subscribe(() =>
        this.getData(this.birdCriteria)
      );
      this.getData(this.birdCriteria);
    });
    merge(
      //this.searchForm.controls.dateRange.valueChanges - Don't listen since the change will affect the from/to dates.
      this.searchForm.controls.fromDate.valueChanges,
      this.searchForm.controls.toDate.valueChanges
      // this.searchForm.controls.graphType.valueChanges, - ignore as this changes the graph model, which will trigger a reload.
    )
      .pipe(debounceTime(100))
      .subscribe((next) => {
        console.log('Date range has changed *****');
        if (this.searchForm.controls.dateRange.value === 'custom') {
          if (
            this.searchForm.controls.fromDate.valid &&
            this.searchForm.controls.toDate.valid
          ) {
            console.log('Regenerating the graph ....');
            this.dateRangeTotal =
              (this.searchForm.controls.toDate.value.getTime() -
                this.searchForm.controls.fromDate.value.getTime()) /
              (1000 * 60 * 60 * 24);
            this.getData(this.birdCriteria);
          } else {
            console.log('Not generating graph as the fromDate is invalid');
          }
        }
      });
  }

  ngOnDestroy(): void {}

  getData(birdCriteria: BirdCriteria): void {
    let graphData: Observable<LegendItem[]> = null;
    //todo get age/date data as per the AgeRange selection **
    switch (this.searchForm.controls.graphType.value) {
      case 'DELTA':
        if (this.searchForm.controls.dateRange.value === 'custom') {
          if (this.searchForm.controls.modelType.value === 'MIN') {
            graphData = of(this.dateRangeDataDeltaMin);
          } else {
            graphData = of(this.dateRangeDataDelta);
          }
          // console.log('use delta cache data');
        } else {
          graphData = this.weightService.getMultiBirdWeightDeltaData(
            birdCriteria,
            this.searchForm.controls.modelType.value
          );
        }
        break;
      default:
        if (this.searchForm.controls.dateRange.value === 'custom') {
          if (this.searchForm.controls.modelType.value === 'MIN') {
            graphData = of(this.dateRangeDataWeightMin);
          } else {
            graphData = of(this.dateRangeDataWeight);
          }
          // console.log('use cache data');
        } else {
          graphData = this.weightService.getMultiBirdData(
            birdCriteria,
            this.searchForm.controls.modelType.value
          );
        }
    }

    graphData.subscribe((legendItems) => {
      //got the data now, fix the format ******
      this.legendItems = legendItems;
      // Set the colours.
      this.legendItems.forEach((legendItem) => {
        if (legendItem.birdID) {
          legendItem.borderColor = this.colorHash.hex(legendItem.birdID);
          if (legendItem.datasets) {
            legendItem.datasets.forEach(
              (ds) => (ds.borderColor = this.colorHash.hex(legendItem.birdID))
            );
          }
        }
      });
      this.redrawGraph();
    });
  }

  getStrokeDash(legend: LegendItem): string {
    return legend.borderDash.join(' ');
  }

  getAdjustedData(legendItems: LegendItem[]) {
    const adjustedDatasets = [];
    const dateMax = parseInt(this.searchForm.controls.dateRange.value, 10);
    if (this.searchForm.controls.dateRange.value !== 'custom') {
      this.legendItems
        .filter((li) => li.datasets)
        .forEach((li) =>
          li.datasets.forEach((ds) => {
            const t: any = new Object();
            Object.assign(t, ds);
            t.data = t.data.filter((el) => el.x <= dateMax);
            adjustedDatasets.push(t);
          })
        );
    } else {
      //todo Prepare the weight data with date range
      this.legendItems
        .filter((li) => li.datasets)
        .forEach((li) =>
          li.datasets.forEach((ds) => {
            const t: any = new Object();
            Object.assign(t, ds);
            //todo reset data to data with Date
            t.data = t.dataDate;
            for (let i = 0; i < t.data.length; i++) {
              t.data[i].x = new Date(t.data[i].x);
            }
            t.data = t.data.filter(
              (el) =>
                el.x <= this.searchForm.controls.toDate.value &&
                el.x >= this.searchForm.controls.fromDate.value
            );
            adjustedDatasets.push(t);
          })
        );
    }
    // console.log('Graph data');
    // console.log(adjustedDatasets);
    return adjustedDatasets;
  }

  redrawGraph(): void {
    // Get rid of the old chart.
    if (this.lineChart) {
      this.lineChart.destroy();
    }

    // We want to filter the data sets by age, and date range
    const adjustedDatasets = this.getAdjustedData(this.legendItems);

    // Draw the new chart.
    if (this.searchForm.controls.dateRange.value !== 'custom') {
      this.lineChart = new Chart(this.lineCanvas.nativeElement, {
        type: 'scatter',
        data: {
          datasets: adjustedDatasets,
        },
        options: {
          events: ['click', 'mousemove', 'mouseout'],
          onClick: (evt, item) => {
            this.chartClickHandlerService.onClick(evt, item, this.lineChart);
          },
          onHover: (evt, item) => {
            this.chartClickHandlerService.chartHoverHandler(evt, item);
          },
          animation: false,
          scales: {
            yAxes: [
              {
                scaleLabel: {
                  display: true,
                  labelString:
                    this.searchForm.controls.graphType.value === 'WEIGHT'
                      ? 'Weight (kg)'
                      : 'Daily Weight Change (%)',
                },
                ticks: {
                  suggestedMin: 0,
                },
              },
            ],
            xAxes: [
              {
                scaleLabel: {
                  display: true,
                  labelString: 'Age (days)',
                },
                ticks: {
                  min: 0,
                },
              },
            ],
          },
          legend: { display: false },
          tooltips: {
            callbacks: {
              label:
                this.searchForm.controls.graphType.value === 'WEIGHT'
                  ? (tooltipItem, data) => {
                      let label =
                        data.datasets[tooltipItem.datasetIndex].label || '';
                      if (label) {
                        label += ': ';
                      }

                      const cropStatus =
                        data.datasets[tooltipItem.datasetIndex].data[
                          tooltipItem.index
                        ].cropStatus;
                      if (cropStatus) {
                        label += '(Crop: ' + cropStatus + ') ';
                      }

                      label += Math.round(tooltipItem.xLabel * 100) / 100;
                      label += ' days, ';
                      label += this.weightService.formatWeightFromKg(
                        tooltipItem.yLabel
                      );
                      return label;
                    }
                  : (tooltipItem, data) => {
                      let label =
                        data.datasets[tooltipItem.datasetIndex].label || '';

                      if (label) {
                        label += ': ';
                      }
                      label += Math.round(tooltipItem.xLabel * 100) / 100;
                      label += ' days, ';
                      label += Math.round(tooltipItem.yLabel * 100) / 100;
                      label += '%';
                      return label;
                    },
            },
          },
        },
      });
    }
    //todo new date chart with date range
    const labels = [];
    const totalDays = this.dateRangeTotal;
    // console.log('check totalDays');
    // console.log(totalDays);
    const labelFreq =
      totalDays <= 31
        ? 'day'
        : totalDays <= 150
        ? 'week'
        : totalDays < 365 * 3
        ? 'month'
        : 'year';
    if (this.searchForm.controls.dateRange.value === 'custom') {
      this.lineChart = new Chart(this.lineCanvas.nativeElement, {
        type: 'scatter',
        data: {
          labels,
          datasets: adjustedDatasets,
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
                  labelString: 'Date',
                },
                type: 'time',
                distribution: 'series',
                time: {
                  unit: labelFreq,
                },
              },
            ],
          },
          legend: { display: false },
          tooltips: {
            callbacks: {
              label: (tooltipItem, data) => {
                let label = data.datasets[tooltipItem.datasetIndex].label || '';
                if (label) {
                  label += ': ';
                }
                // const cropStatus =
                //   data.datasets[tooltipItem.datasetIndex].data[
                //     tooltipItem.index
                //   ].cropStatus;
                // if (cropStatus) {
                //   label += '(Crop: ' + cropStatus + ') ';
                // }
                //
                // if (isNaN(tooltipItem.xLabel)) {
                //   // TODO h is not in 24 time - does that matter?
                //   label +=
                //     moment(new Date(tooltipItem.xLabel)).format(
                //       'DD/MM/YY HH:mm'
                //     ) + ', ';
                // } else {
                //   label += Math.round(tooltipItem.xLabel * 100) / 100;
                //   label += ' days, ';
                // }
                // label += this.weightService.formatWeightFromGrams(
                //   tooltipItem.yLabel
                // );
                return label;
              },
            },
          },
        },
      });
    }
  }

  setHidden(legendItem: LegendItem, hidden: boolean) {
    if (!legendItem.referenceData) {
      legendItem.hidden = hidden;
      legendItem.datasets.forEach((ds) => (ds.hidden = hidden));
    }
  }

  selectAll(): void {
    this.legendItems.forEach((legendItem) => this.setHidden(legendItem, false));
    this.redrawGraph();
  }

  unselectAll(): void {
    this.legendItems.forEach((legendItem) => this.setHidden(legendItem, true));
    this.redrawGraph();
  }

  toggle(legendItem: LegendItem): void {
    this.setHidden(legendItem, !legendItem.hidden);
    this.redrawGraph();
  }
}
