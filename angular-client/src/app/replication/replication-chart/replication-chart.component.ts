import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Chart } from 'chart.js';
import { FileActivity } from '../file-activity';

@Component({
  selector: 'app-replication-chart',
  templateUrl: './replication-chart.component.html',
})
export class ReplicationChartComponent implements OnInit {
  @ViewChild('chartCanvas', { static: true })
  chartCanvas: any;

  chart: any;
  chartData = [];
  chartLabels = [];

  @Input()
  fileActivity: FileActivity;

  constructor() {}

  ngOnInit() {
    const data = {
      labels: this.chartLabels,
      datasets: [
        {
          label: '',
          data: this.chartData,
          pointRadius: 0,
        },
      ],
    };

    const options = {
      maintainAspectRatio: false,
      responsive: true,
      legend: {
        display: false,
      },
      tooltips: {
        callbacks: {
          label: (tooltipItem) => tooltipItem.yLabel,
        },
      },
      scales: {
        yAxes: [
          {
            ticks: {
              suggestedMin: 0,
              suggestedMax: 50,
            },
          },
        ],
        xAxes: [
          {
            gridLines: {
              color: 'rgba(0, 0, 0, 0)',
            },
          },
        ],
      },
    };

    this.chart = new Chart(this.chartCanvas.nativeElement, {
      type: 'line',
      data,
      options,
    });
  }

  updateChart(): void {
    if (this.fileActivity) {
      this.chartData.length = 0;
      this.chartLabels.length = 0;

      for (const n of this.fileActivity.fileActivity) {
        this.chartData.push(n);
      }

      for (let i = 0; i < this.fileActivity.maxSize; i++) {
        this.chartLabels.push('');
      }

      this.chart.update();
    }
  }
}
