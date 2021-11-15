import { Chart } from 'chart.js';
import { Injectable } from '@angular/core';
import { RouterUtilsService } from '../../../common/router-utils.service';

@Injectable({
  providedIn: 'root',
})
export class ChartClickHandlerService {
  constructor(private routerUtilsService: RouterUtilsService) {}

  onClick(evt: any, item: any, chart: Chart) {
    if (item && item.length) {
      const selectedItem = item[0];
      const datasetIndex = selectedItem._datasetIndex;
      const seriesIndex = selectedItem._index;

      const datasetLabel = chart.data.datasets[datasetIndex].label;
      const seriesValue: any =
        chart.data.datasets[datasetIndex].data[seriesIndex];

      const ctrlKey = evt && evt.ctrlKey;
      console.log(
        'Selected: ' +
          ' ctrlKey = ' +
          ctrlKey +
          ': ' +
          datasetLabel +
          ' value = ' +
          seriesValue.recordId
      );

      if (seriesValue && seriesValue.recordId) {
        this.routerUtilsService.navigateWithNewTab(
          'record/' + seriesValue.recordId,
          ctrlKey
        );
      }
    }
  }

  chartHoverHandler(event: any, chartElement: any) {
    event.target.style.cursor = chartElement[0] ? 'pointer' : 'default';
  }
}
