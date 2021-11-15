import { Component, Input, OnInit } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import { LocationService } from '../../location/location.service';
import {
  catchError,
  switchMap,
  filter,
  tap,
  pairwise,
  distinctUntilChanged,
  startWith,
} from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-nestobservation-edit-form',
  templateUrl: './nestobservation-edit-form.component.html',
})
export class NestObservationEditFormComponent implements OnInit {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  constructor(
    public optionsService: OptionsService,
    private locationService: LocationService
  ) {}

  ngOnInit() {
    this.myFormGroup
      .get('locationID')
      .valueChanges.pipe(
        startWith(this.myFormGroup.get('locationID').value),
        distinctUntilChanged(),
        pairwise<string>(),
        filter(([_, newValue]) => newValue !== null && newValue !== ''),
        switchMap(([_, newValue]) =>
          this.locationService
            .findById(newValue)
            .pipe(catchError(() => of(null)))
        ),
        filter((val) => val !== null)
      )
      .subscribe((result) => {
        if (result.birdID) {
          this.myFormGroup.get('birdID').setValue(result.birdID);
        }
      });
  }
}
