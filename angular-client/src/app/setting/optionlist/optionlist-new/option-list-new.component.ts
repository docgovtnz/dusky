import { combineLatest } from 'rxjs/index';

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup } from '@angular/forms';
import { OptionListService } from '../option-list.service';
import { OptionListItemNewEntity } from '../../../domain/optionListItemNew.entity';
import { OptionsService } from '../../../common/options.service';
import { ValidationMessage } from '../../../domain/response/validation-message';
import { FormService } from '../../../form/form.service';
import { BaseEntityFormFactory } from '../../../form/base-entity-form-factory';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-option-list-new',
  templateUrl: 'option-list-new.component.html',
})
export class OptionListNewComponent implements OnInit {
  // New Form layout
  private _optionListItemNewEntity: OptionListItemNewEntity;
  private formFactory: BaseEntityFormFactory;
  messages: ValidationMessage[];

  myFormGroup: FormGroup;

  listName = '';
  pageTitle = '';
  optionList: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public optionsService: OptionsService,
    private formService: FormService,
    private service: OptionListService
  ) {}

  ngOnInit(): void {
    // Take the latest value from each of the supplied observables and execute the subscriber
    // function. Note: the subscribe method isn't called until all observables have issued at
    // least one value. This subscription will keep being fired when there are more values,
    // however the createFormFactory() observable should only issue one value.
    combineLatest(
      this.route.params,
      this.route.queryParams,
      this.formService.createFormFactory('OptionListItemNewEntity'),
      this.optionsService.getOptionListTitles()
    ).subscribe(([params, queryParams, formFactory, titles]) => {
      //console.log(params);
      this.listName = params['name'];
      this.formFactory = formFactory;
      this.newEntity();
      this.pageTitle = titles[this.listName];
      this.optionsService.getOptions(this.listName).subscribe((list) => {
        list.shift();
        this.optionList = list;
        //console.log(this.optionList);
      });
    });
  }

  get optionListItemNewEntity(): OptionListItemNewEntity {
    return this._optionListItemNewEntity;
  }

  set optionListItemNewEntity(value: OptionListItemNewEntity) {
    this._optionListItemNewEntity = value;
    if (this.formFactory) {
      this.myFormGroup = this.formFactory.createForm(value);
      this.myFormGroup.patchValue(this._optionListItemNewEntity);
    }
  }

  newEntity() {
    const e: OptionListItemNewEntity = new OptionListItemNewEntity();
    // set value after init so that form gets patched correctly
    this.optionListItemNewEntity = e;
  }

  onSave(): void {
    this.messages = null;
    Object.assign(this.optionListItemNewEntity, this.myFormGroup.getRawValue());
    console.log(this.optionListItemNewEntity.name);
    const newItem = this.optionListItemNewEntity.name.trim();
    if (newItem === '') {
      const msg: ValidationMessage = {
        key: 'emptyItem',
        messageText: 'New item must not be an empty string.',
        propertyName: 'name',
        messageParameters: null,
      };
      this.messages = [];
      this.messages.push(msg);
      return;
    }
    //newItem = newItem.charAt(0).toUpperCase() + newItem.substring(1);
    let existing = false;
    for (let i = 0; i < this.optionList.length; i++) {
      if (
        newItem.toLowerCase().trim() === this.optionList[i].toLowerCase().trim()
      ) {
        const msg: ValidationMessage = {
          key: 'existingItem',
          messageText:
            'A ' +
            this.pageTitle +
            ' with the same name already exists. Please choose another name.',
          propertyName: 'name',
          messageParameters: null,
        };
        this.messages = [];
        this.messages.push(msg);
        existing = true;
        break;
      }
    }
    if (!existing) {
      const newList = [];
      for (let i = 0; i < this.optionList.length; i++) {
        newList.push(this.optionList[i]);
      }
      newList.push(newItem);
      newList.sort((a, b) => (a > b ? 1 : -1));
      //console.log(newList);
      const listToSave = [];
      for (let i = 0; i < newList.length; i++) {
        const listItem = {
          text: newList[i],
        };
        listToSave.push(listItem);
      }

      const newOptionList = {
        name: this.listName,
        optionListItemList: listToSave,
      };

      this.service.save(newOptionList).subscribe((response) => {
        console.log(response);
        this.messages = response.messages;
        if (this.messages === null) {
          this.refreshOptionList();
          this.onCancel();
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/settings/optionlist/' + this.listName]);
  }

  refreshOptionList(): void {
    const x = this.optionsService.getCaches();
    for (let i = 0; i < x.length - 1; i++) {
      if (x[i].cacheName === 'Option Lists') {
        console.log(x[i]);
        x[i].forceRefresh();
      }
    }
  }
}
