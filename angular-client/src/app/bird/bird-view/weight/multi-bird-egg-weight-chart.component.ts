import { Component, OnInit, ViewChild } from '@angular/core';
import { WeightService } from './weight.service';
import { BirdService } from '../../bird.service';
import { SettingService } from '../../../setting/setting.service';
import { OptionsService } from '../../../common/options.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BirdCriteria } from '../../bird.criteria';
import { Subscription } from 'rxjs';
import { Chart } from 'chart.js';
import * as ColorHash from 'color-hash';
import { ChartClickHandlerService } from './chart-click-handler.service';
import { LegendItem } from './legend-item';
import { Dataset } from './dataset';

@Component({
  selector: 'app-multi-bird-egg-weight-chart',
  templateUrl: 'multi-bird-egg-weight-chart.component.html',
})
export class MultiBirdEggWeightChartComponent implements OnInit {
  legendItems: LegendItem[];

  @ViewChild('multiBirdWeightCanvas', { static: true }) lineCanvas;
  lineChart: any;

  routeSubscription: Subscription;
  birdCriteria: BirdCriteria;
  colorHash = new ColorHash();

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
    this.routeSubscription = this.route.queryParams.subscribe((params) => {
      // run if the user refreshed the page or there was a call to route.navigate (not location.go())
      this.birdCriteria = new BirdCriteria();
      // set defaults before populating from params
      this.birdCriteria.showAlive = true;
      this.birdCriteria.populateFromParams(params);
      this.getData(this.birdCriteria);
    });
  }

  getData(birdCriteria: BirdCriteria): void {
    this.weightService
      .getMultiBirdEggData(birdCriteria)
      .subscribe((legendItems) => {
        this.legendItems = legendItems;

        // Set the colours.
        this.legendItems.forEach((legendItem) => {
          if (legendItem.birdID) {
            legendItem.datasets.forEach(
              (ds) => (ds.borderColor = this.colorHash.hex(legendItem.birdID))
            );
            legendItem.borderColor = this.colorHash.hex(legendItem.birdID);
          }
        });

        this.redrawGraph();
      });
  }

  getStrokeDash(dataset: Dataset): string {
    return dataset.borderDash.join(' ');
  }

  redrawGraph(): void {
    // Get rid of the old chart.
    if (this.lineChart) {
      this.lineChart.destroy();
    }

    // We want to filter the data sets by age.
    const datasets = [];
    this.legendItems.forEach((legendItem) =>
      datasets.push(...legendItem.datasets)
    );
    console.log(datasets);

    // Draw the new chart.
    this.lineChart = new Chart(this.lineCanvas.nativeElement, {
      type: 'scatter',
      data: {
        datasets,
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
                labelString: 'Weight (g)',
              },
              ticks: {},
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
            label: (tooltipItem, data) => {
              let label = data.datasets[tooltipItem.datasetIndex].label || '';

              if (label) {
                label += ': ';
              }
              label += Math.round(tooltipItem.xLabel * 100) / 100;
              label += ' days / ';
              label += Math.round(tooltipItem.yLabel * 100) / 100;
              label += 'g';
              return label;
            },
          },
        },
      },
    });
  }

  setHidden(legendItem: LegendItem, hidden: boolean) {
    legendItem.hidden = hidden;
    legendItem.datasets.forEach((ds) => (ds.hidden = hidden));
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

  /*
  getLegendType(dataset: Dataset): string {
    const relatedDS = this.datasets.filter(ds => ds.birdID === dataset.birdID);
    if (relatedDS.length === 2) {
      return 'BOTH';
    } else if (relatedDS.length === 1) {
      if (relatedDS[0].borderDash.length === 0) {
        return 'WEIGHT';
      } else {
        return 'REF';
      }
    }
  }
  */
}
