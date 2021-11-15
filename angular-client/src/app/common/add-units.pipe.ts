import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'addUnits',
})
export class AddUnitsPipe implements PipeTransform {
  transform(value: number, units: string): string {
    return value || value === 0 ? `${value} ${units}` : null;
  }
}
