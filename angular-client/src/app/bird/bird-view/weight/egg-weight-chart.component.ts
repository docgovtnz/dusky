import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { SettingService } from '../../../setting/setting.service';
import { WeightService } from './weight.service';
import { forkJoin } from 'rxjs';
import { EggRefData } from './egg-ref-data';
import { EggWeightDTO } from './egg-weight-dto';
import { Chart } from 'chart.js';
import { ChartClickHandlerService } from './chart-click-handler.service';
import { DataSet } from './egg-weight-chart-dataset';

@Component({
  selector: 'app-egg-weight-chart',
  templateUrl: 'egg-weight-chart.component.html',
})
export class EggWeightChartComponent implements OnInit {
  @Input()
  birdID: string;

  @ViewChild('eggWeightCanvas', { static: true }) lineCanvas;
  lineChart: any;

  eggRefData: EggRefData[];

  eggData: EggWeightDTO[];

  status: 'LOADING' | 'READY' | 'ERROR' | 'WARNING';

  messages: string[] = [];

  constructor(
    private weightService: WeightService,
    private settingService: SettingService,
    private chartClickHandlerService: ChartClickHandlerService
  ) {}

  ngOnInit(): void {
    this.status = 'LOADING';

    forkJoin([
      this.weightService.getEggData(this.birdID),
      this.weightService.getEggRefData(this.birdID),
    ]).subscribe((results) => {
      this.messages = [];

      if (results[0].messages && results[0].messages.length > 0) {
        results[0].messages.forEach((message) =>
          this.messages.push(message.messageText)
        );
        this.status = 'ERROR';
      } else {
        this.eggData = results[0].model;

        if (results[1].messages && results[1].messages.length > 0) {
          results[1].messages.forEach((message) =>
            this.messages.push(message.messageText)
          );
          this.status = 'WARNING';
        } else {
          this.eggRefData = results[1].model;
          this.status = 'READY';
        }

        this.generateGraph();
      }
    });
  }

  generateGraph(): void {
    // If there was an old graph, get rid of it.
    if (this.lineChart) {
      this.lineChart.destroy();
    }

    const dataSets: any[] = [];

    // Convert the data into datasets.
    let max = 0;
    if (this.eggData) {
      const actualWeights: { x; y; recordId }[] = [];
      this.eggData.forEach((dataPoint) => {
        actualWeights.push({
          x: dataPoint.ageInDays,
          y: dataPoint.weightInGrams,
          recordId: dataPoint.recordID,
        }); // watch out for the difference in Id to ID here
        if (dataPoint.weightInGrams > max) {
          max = dataPoint.weightInGrams;
        }
      });
      dataSets.push(
        new DataSet('Actual Egg Weight', 'rgba(0,0,0,1)', actualWeights)
      );
    }

    if (this.eggRefData) {
      const predictedWeights: { x; y }[] = [];
      this.eggRefData.forEach((dataPoint) => {
        predictedWeights.push({
          x: dataPoint.ageInDays,
          y: dataPoint.predictedWeight,
        });
        if (dataPoint.predictedWeight > max) {
          max = dataPoint.predictedWeight;
        }
      });
      dataSets.push(
        new DataSet(
          'Predicted Egg Weight',
          'rgba(255,255,0,1)',
          predictedWeights
        )
      );
    }

    this.lineChart = new Chart(this.lineCanvas.nativeElement, {
      type: 'scatter',
      data: {
        datasets: dataSets,
      },
      options: {
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
              ticks: {
                suggestedMax: max,
              },
            },
          ],
          xAxes: [
            {
              scaleLabel: {
                display: true,
                labelString: 'Age (days)',
              },
              type: 'linear',
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
              label += Math.round(tooltipItem.xLabel * 100) / 100;
              label += ' days, ';
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
}
