import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], label: string, value: string): any[] {
    console.log('Filtering:', items, label, value);
    if (!items) {
      return [];
    }
    if (!value) {
      return items;
    }
    if (value === '' || value === null) {
      return [];
    }
    return items.filter((e) => e[label].indexOf(value) > -1);
  }
}
