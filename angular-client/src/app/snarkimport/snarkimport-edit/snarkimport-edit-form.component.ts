import { Subscription } from 'rxjs/index';
import {
  Component,
  Input,
  OnInit,
  Output,
  EventEmitter,
  ViewChild,
  OnDestroy,
  ElementRef,
} from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { FormGroup } from '@angular/forms';
import { LocationService } from '../../location/location.service';
import { InputService } from '../../common/input.service';

@Component({
  selector: 'app-snarkimport-edit-form',
  templateUrl: './snarkimport-edit-form.component.html',
  styleUrls: ['./snarkimport-edit.component.scss'],
})
export class SnarkImportEditFormComponent implements OnInit, OnDestroy {
  // New Form layout
  @Input()
  myFormGroup: FormGroup;

  @Output()
  snarkFileChange = new EventEmitter();

  @ViewChild('snarkFile') snarkFileInput: ElementRef;

  private island: string = null;

  private clearInputListener: Subscription;

  constructor(
    public optionsService: OptionsService,
    private clearInputService: InputService,
    private locationService: LocationService
  ) {
    this.clearInputListener = clearInputService.clearInputRequest$.subscribe(
      () => {
        // clear out the file input
        this.snarkFileInput.nativeElement.value = null;
      }
    );
  }

  ngOnInit() {
    // Obtain island from chosen location and pass through to disabled text field (for consistent display)
    this.myFormGroup.get('locationID').valueChanges.subscribe((locationID) => {
      if (locationID) {
        this.locationService
          .findById(locationID)
          .subscribe((location) => (this.island = location.island));
      }
    });
  }

  ngOnDestroy() {
    this.clearInputListener.unsubscribe();
  }

  onSnarkFileChange(event: any) {
    const files = event.target.files;
    if (files.length === 0) {
      this.snarkFileChange.emit(null);
    } else {
      this.snarkFileChange.emit(event.target.files[0]);
    }
  }
}
