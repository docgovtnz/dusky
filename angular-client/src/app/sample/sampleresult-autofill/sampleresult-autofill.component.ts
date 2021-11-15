import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OptionsService } from '../../common/options.service';
import { Router } from '@angular/router';
import { SampleResultAutofill } from '../SampleResultAutofill';

@Component({
  selector: 'app-sampleresult-autofill',
  templateUrl: './sampleresult-autofill.component.html',
})
export class SampleResultAutofillComponent implements OnInit {
  @Input()
  optionsListName: string;

  @Output()
  autofillSubmitted = new EventEmitter<SampleResultAutofill>();

  sampleResultAutofill = new SampleResultAutofill();
  optionList: string[];
  standardAssayOption: string;

  constructor(private router: Router, public optionsService: OptionsService) {}

  ngOnInit() {
    this.optionsService
      .getOptions(this.optionsListName)
      .subscribe((response) => {
        this.optionList = response;
      });
  }

  onStandardAssayOptionChange(optionListText) {
    this.optionsService
      .getOptionList(this.optionsListName, optionListText)
      .subscribe((optionList: string[]) => {
        this.sampleResultAutofill.tests = optionList;
      });
  }

  onSubmit() {
    this.autofillSubmitted.emit(this.sampleResultAutofill);
  }
}
