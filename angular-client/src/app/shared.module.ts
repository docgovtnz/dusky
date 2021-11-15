import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AlertModule } from 'ngx-bootstrap/alert';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ButtonsModule } from 'ngx-bootstrap/buttons';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { ModalModule } from 'ngx-bootstrap/modal';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { ProgressbarModule } from 'ngx-bootstrap/progressbar';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { TypeaheadModule } from 'ngx-bootstrap/typeahead';
import { NgProgressModule } from 'ngx-progressbar';
import { NgProgressHttpModule } from 'ngx-progressbar/http';

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { BirdIdSelectMultiComponent } from './bird/bird-id-select-multi/bird-id-select-multi.component';
import { BirdNameIdSelectComponent } from './bird/bird-name-id-select/bird-name-id-select.component';
import { BirdNameIdSelectControlComponent } from './bird/bird-name-id-select-control/bird-name-id-select-control.component';
import { BirdNameLabelComponent } from './bird/bird-name-label/bird-name-label.component';
import { BirdNameSelectMultiComponent } from './bird/bird-name-select-multi/bird-name-select-multi.component';
import { BirdNameLabelMultiComponent } from './bird/bird-name-label-multi/bird-name-label-multi.component';

import { AddUnitsPipe } from './common/add-units.pipe';
import { BannerComponent } from './common/banner/banner.component';
import { ButtonBlockComponent } from './common/buttonblock/buttonblock.component';
import { CommentsEditComponent } from './common/comments-edit/comments-edit.component';
import { CommentsViewComponent } from './common/comments-view/comments-view.component';
import { ControlBlockComponent } from './common/controlblock/controlblock.component';
import { DateControlComponent } from './common/date-control/date-control.component';
import { DateEditorComponent } from './common/date-editor/date-editor.component';
import { DateTimeControlComponent } from './common/date-time-control/date-time-control.component';
import { DeleteConfirmationPopupComponent } from './common/delete-confirmation-popup/delete-confirmation-popup.component';
import { FilterPipe } from './common/filter.pipe';
import { LinkViewComponent } from './common/link-view/link-view.component';
import { MyLinkDirective } from './common/my-link.directive';
import { MessageDisplayComponent } from './form/message-display/message-display.component';
import { ServerMessageDisplayComponent } from './common/server-message-display/server-message-display.component';
import { ToggleSwitchComponent } from './common/toggle-switch/toggle-switch.component';
import { TypeAheadControlComponent } from './common/type-ahead-control/type-ahead-control.component';
import { TypeAheadComponent } from './common/type-ahead/type-ahead.component';
import { OptionMultiSelectComponent } from './common/option-multi-select/option-multi-select.component';

import { LocationNameIdSelectControlComponent } from './common/location-name-id-select-control/location-name-id-select-control.component';
import { LocationIdSelectMultiComponent } from './common/location-name-id-select-multi/location-id-select-multi.component';
import { LocationNameSelectMultiComponent } from './common/location-name-select-multi/location-name-select-multi.component';
import { LocationNameLabelComponent } from './common/location-name-label/location-name-label.component';
import { LocationNameIdSelectComponent } from './common/location-name-id-select/location-name-id-select.component';

import { PersonNameLabelComponent } from './common/person-name-label/person-name-label.component';
import { PersonNameIdSelectControlComponent } from './common/person-name-id-select-control/person-name-id-select-control.component';
import { PersonIdSelectComponent } from './common/person-id-select/person-id-select.component';

import { ObserverViewComponent } from './common/observer-view/observer-view.component';
import { ObserverEditComponent } from './common/observer-edit/observer-edit.component';

import { TransmitterIdLabelComponent } from './common/transmitter-id-label/transmitter-id-label.component';

import { RevisionListViewComponent } from './common/revision-list-view/revision-list-view.component';

import { IfPermissionDirective } from './authentication/permission/IfPermission.directive';

import { EditControlComponent } from './form/edit-control/edit-control.component';
import { WidgetComponent } from './form/widget/widget.component';
import { CellWidgetComponent } from './form/cell-widget/cell-widget.component';

import { BirdFeaturePanelComponent } from './bird/birdfeature-view/birdfeature-panel.component';
import { BirdFeatureEditFormComponent } from './bird/birdfeature-edit/birdfeature-edit-form.component';

import { MeasureDetailEditFormComponent } from './measuredetail/measuredetail-edit/measuredetail-edit-form.component';
import { MeasureDetailPanelComponent } from './measuredetail/measuredetail-view/measuredetail-panel.component';
import { AutofocusDirective } from './common/autofocus.directive';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ModalModule,
    BsDropdownModule,
    FontAwesomeModule,
    AlertModule,
    ButtonsModule,
    RouterModule,
    BsDatepickerModule,
    TypeaheadModule,
    PaginationModule,
    CollapseModule,
    TabsModule,
    ProgressbarModule,
    NgProgressModule,
    NgProgressHttpModule,
  ],
  declarations: [
    BannerComponent,
    ControlBlockComponent,
    ButtonBlockComponent,
    DeleteConfirmationPopupComponent,
    RevisionListViewComponent,
    DateEditorComponent,
    MyLinkDirective,
    TypeAheadComponent,
    OptionMultiSelectComponent,
    MessageDisplayComponent,
    ServerMessageDisplayComponent,
    DateControlComponent,
    TypeAheadControlComponent,
    DateTimeControlComponent,
    FilterPipe,
    LinkViewComponent,
    ToggleSwitchComponent,
    AddUnitsPipe,
    CommentsEditComponent,
    CommentsViewComponent,
    BirdNameIdSelectComponent,
    BirdNameSelectMultiComponent,
    BirdIdSelectMultiComponent,
    BirdNameIdSelectControlComponent,
    BirdNameLabelComponent,
    BirdNameLabelMultiComponent,
    LocationNameIdSelectControlComponent,
    LocationIdSelectMultiComponent,
    LocationNameSelectMultiComponent,
    LocationNameLabelComponent,
    LocationNameIdSelectComponent,
    PersonNameLabelComponent,
    PersonNameIdSelectControlComponent,
    PersonIdSelectComponent,
    IfPermissionDirective,
    ObserverViewComponent,
    ObserverEditComponent,
    EditControlComponent,
    WidgetComponent,
    CellWidgetComponent,
    BirdFeaturePanelComponent,
    BirdFeatureEditFormComponent,
    MeasureDetailPanelComponent,
    MeasureDetailEditFormComponent,
    TransmitterIdLabelComponent,
    AutofocusDirective,
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ModalModule,
    BsDropdownModule,
    FontAwesomeModule,
    AlertModule,
    ButtonsModule,
    RouterModule,
    BsDatepickerModule,
    TypeaheadModule,
    PaginationModule,
    CollapseModule,
    TabsModule,
    BannerComponent,
    ControlBlockComponent,
    ButtonBlockComponent,
    DeleteConfirmationPopupComponent,
    RevisionListViewComponent,
    DateEditorComponent,
    MyLinkDirective,
    TypeAheadComponent,
    OptionMultiSelectComponent,
    MessageDisplayComponent,
    ServerMessageDisplayComponent,
    DateControlComponent,
    TypeAheadControlComponent,
    DateTimeControlComponent,
    FilterPipe,
    LinkViewComponent,
    ToggleSwitchComponent,
    AddUnitsPipe,
    CommentsEditComponent,
    CommentsViewComponent,
    BirdNameIdSelectComponent,
    BirdNameSelectMultiComponent,
    BirdIdSelectMultiComponent,
    BirdNameIdSelectControlComponent,
    BirdNameLabelComponent,
    BirdNameLabelMultiComponent,
    LocationNameIdSelectControlComponent,
    LocationIdSelectMultiComponent,
    LocationNameSelectMultiComponent,
    LocationNameLabelComponent,
    LocationNameIdSelectComponent,
    PersonNameLabelComponent,
    PersonNameIdSelectControlComponent,
    PersonIdSelectComponent,
    IfPermissionDirective,
    ObserverViewComponent,
    ObserverEditComponent,
    EditControlComponent,
    WidgetComponent,
    CellWidgetComponent,
    BirdFeaturePanelComponent,
    BirdFeatureEditFormComponent,
    MeasureDetailPanelComponent,
    MeasureDetailEditFormComponent,
    TransmitterIdLabelComponent,
    ProgressbarModule,
    NgProgressModule,
    NgProgressHttpModule,
    AutofocusDirective,
  ],
})
export class SharedModule {}
