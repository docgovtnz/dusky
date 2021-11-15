import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BatteryLifeEditFormComponent } from './batterylife-edit/batterylife-edit-form.component';
import { BatteryLifePanelComponent } from './batterylife-view/batterylife-panel.component';
import { BloodSampleDetailEditFormComponent } from './bloodsampledetail-edit/bloodsampledetail-edit-form.component';
import { BloodSampleDetailPanelComponent } from './bloodsampledetail-view/bloodsampledetail-panel.component';
import { BulkSignalEntryEditComponent } from './bulksignalentry-edit/bulksignalentry-edit.component';
import { BulkSignalEntryEditFormComponent } from './bulksignalentry-edit/bulksignalentry-edit-form.component';
import { CaptureDetailEditFormComponent } from './capturedetail-edit/capturedetail-edit-form.component';
import { CaptureDetailPanelComponent } from './capturedetail-view/capturedetail-panel.component';
import { CheckmateDataRowComponent } from './checkmate-edit/checkmate-data-row.component';
import { CheckmateEditFormComponent } from './checkmate-edit/checkmate-edit-form.component';
import { CheckmateErrolDataRowComponent } from './checkmate-edit/checkmate-errol-data-row.component';
import { CheckmateQualityComponent } from './checkmate-edit/checkmate-quality.component';
import { CheckmatePanelComponent } from './checkmate-view/checkmate-panel.component';
import { ChickHealthEditFormComponent } from './chickhealth-edit/chickhealth-edit-form.component';
import { ChickHealthPanelComponent } from './chickhealth-view/chickhealth-panel.component';
import { EggHealthEditFormComponent } from './egghealth-edit/egghealth-edit-form.component';
import { EggHealthPanelComponent } from './egghealth-view/egghealth-panel.component';
import { DateTimeEditorComponent } from './date-time-editor/date-time-editor.component';
import { DualControlWidgetComponent } from './eggtimer-edit/dual-control-widget.component';
import { EggTimerEditFormComponent } from './eggtimer-edit/eggtimer-edit-form.component';
import { ActivityLabelComponent } from './eggtimer-view/activity-label/activity-label.component';
import { EggTimerPanelComponent } from './eggtimer-view/eggtimer-panel.component';
import { HandRaiseEditFormComponent } from './handraise-edit/handraise-edit-form.component';
import { HandRaisePanelComponent } from './handraise-view/handraise-panel.component';
import { HealthCheckEditFormComponent } from './healthcheck-edit/healthcheck-edit-form.component';
import { HealthCheckPanelComponent } from './healthcheck-view/healthcheck-panel.component';
import { HealthStatusEditFormComponent } from './healthstatus-edit/healthstatus-edit-form.component';
import { HealthStatusPanelComponent } from './healthstatus-view/healthstatus-panel.component';
import { IdentificationEditComponent } from './identification-edit/identification-edit.component';
import { TransmitterTxidIdSelectControlComponent } from './identification-edit/transmitter-txid-id-select-control/transmitter-txid-id-select-control.component';
import { IdentificationComponent } from './identification-view/identification.component';
import { LocationBearingEditFormComponent } from './locationbearing-edit/locationbearing-edit-form.component';
import { LocationBearingPanelComponent } from './locationbearing-view/locationbearing-panel.component';
import { MedicationEditFormComponent } from './medication-edit/medication-edit-form.component';
import { MedicationPanelComponent } from './medication-view/medication-panel.component';
import { MultiSelectControlComponent } from './multi-select-control/multi-select-control.component';
import { MultiValueViewComponent } from './multi-value-view/multi-value-view.component';
import { OtherSampleEditFormComponent } from './othersample-edit/othersample-edit-form.component';
import { OtherSamplePanelComponent } from './othersample-view/othersample-panel.component';
import { RecordEditFormComponent } from './record-edit/record-edit-form.component';
import { RecordEditComponent } from './record-edit/record-edit.component';
import { RecordObserverNameLabelComponent } from './record-observer-name-label/record-observer-name-label.component';
import { RecordSearchComponent } from './record-search/record-search.component';
import { OptionSelectMultiComponent } from './record-search/option-select-multi/option-select-multi.component';
import { RecordPanelComponent } from './record-view/record-panel.component';
import { RecordViewComponent } from './record-view/record-view.component';
import { SnarkDataEditFormComponent } from './snarkdata-edit/snarkdata-edit-form.component';
import { SnarkDataPanelComponent } from './snarkdata-view/snarkdata-panel.component';
import { SpermSampleEditFormComponent } from './spermsample-edit/spermsample-edit-form.component';
import { SpermSamplePanelComponent } from './spermsample-view/spermsample-panel.component';
import { StandardEditFormComponent } from './standard-edit/standard-edit-form.component';
import { StandardPanelComponent } from './standard-view/standard-panel.component';
import { SupplementaryFeedingEditFormComponent } from './supplementaryfeeding-edit/supplementaryfeeding-edit-form.component';
import { SupplementaryFeedingPanelComponent } from './supplementaryfeeding-view/supplementaryfeeding-panel.component';
import { SwabSampleEditFormComponent } from './swabsample-edit/swabsample-edit-form.component';
import { SwabSamplePanelComponent } from './swabsample-view/swabsample-panel.component';
import { TransferDetailEditFormComponent } from './transferdetail-edit/transferdetail-edit-form.component';
import { TransferDetailPanelComponent } from './transferdetail-view/transferdetail-panel.component';
import { TxActivityEditFormComponent } from './txactivity-edit/txactivity-edit-form.component';
import { TxActivityPanelComponent } from './txactivity-view/txactivity-panel.component';
import { WeightEditFormComponent } from './weight-edit/weight-edit-form.component';
import { WeightPanelComponent } from './weight-view/weight-panel.component';

import { RecordRoutingModule } from './record.routes';

import { MapModule } from '../map/map.module';
import { SharedModule } from '../shared.module';

@NgModule({
  imports: [CommonModule, SharedModule, MapModule, RecordRoutingModule],
  declarations: [
    RecordSearchComponent,
    RecordViewComponent,
    RecordPanelComponent,
    RecordEditComponent,
    RecordEditFormComponent,
    StandardEditFormComponent,
    StandardPanelComponent,
    MultiSelectControlComponent,
    MultiValueViewComponent,
    WeightPanelComponent,
    IdentificationComponent,
    WeightEditFormComponent,
    IdentificationEditComponent,
    HealthCheckEditFormComponent,
    HealthCheckPanelComponent,
    CaptureDetailEditFormComponent,
    CaptureDetailPanelComponent,
    TransferDetailEditFormComponent,
    TransferDetailPanelComponent,
    CheckmatePanelComponent,
    HandRaisePanelComponent,
    SupplementaryFeedingPanelComponent,
    SnarkDataPanelComponent,
    CheckmateEditFormComponent,
    CheckmateDataRowComponent,
    CheckmateErrolDataRowComponent,
    CheckmateQualityComponent,
    HandRaiseEditFormComponent,
    SupplementaryFeedingEditFormComponent,
    SnarkDataEditFormComponent,
    BatteryLifePanelComponent,
    BatteryLifeEditFormComponent,
    MedicationPanelComponent,
    MedicationEditFormComponent,
    BloodSampleDetailPanelComponent,
    BloodSampleDetailEditFormComponent,
    SwabSamplePanelComponent,
    SwabSampleEditFormComponent,
    OtherSamplePanelComponent,
    OtherSampleEditFormComponent,
    SpermSamplePanelComponent,
    SpermSampleEditFormComponent,
    RecordObserverNameLabelComponent,
    OptionSelectMultiComponent,
    EggTimerPanelComponent,
    EggTimerEditFormComponent,
    HealthStatusEditFormComponent,
    HealthStatusPanelComponent,
    TransmitterTxidIdSelectControlComponent,
    TxActivityPanelComponent,
    TxActivityEditFormComponent,
    DualControlWidgetComponent,
    ChickHealthEditFormComponent,
    ChickHealthPanelComponent,
    EggHealthEditFormComponent,
    EggHealthPanelComponent,
    ActivityLabelComponent,
    LocationBearingPanelComponent,
    LocationBearingEditFormComponent,
    BulkSignalEntryEditComponent,
    BulkSignalEntryEditFormComponent,
    DateTimeEditorComponent,
  ],
  exports: [CommonModule],
})
export class RecordModule {}
