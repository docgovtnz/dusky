// Angular modules

import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule, ErrorHandler } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';

// External modules

import { JwtModule } from '@auth0/angular-jwt';
import { fas } from '@fortawesome/free-solid-svg-icons';
import {
  FontAwesomeModule,
  FaIconLibrary,
  FaConfig,
} from '@fortawesome/angular-fontawesome';
import * as Sentry from '@sentry/angular';
import { AlertModule } from 'ngx-bootstrap/alert';
import { defineLocale } from 'ngx-bootstrap/chronos';
import { enGbLocale } from 'ngx-bootstrap/locale';
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

// Top-level components

import { PageNotFoundComponent } from './application/page-not-found.component';
import { DashboardComponent } from './application/dashboard/dashboard.component';
import { HomeComponent } from './application/home/home.component';
import { SelectRecordTypeComponent } from './application/select-record-type/select-record-type.component';
import { IndexRebuildComponent } from './application/index-rebuild/index-rebuild.component';

// Authentication

import { AuthGuard } from './authentication/auth.guard';
import { LoginComponent } from './authentication/login/login.component';

// Error handling

import { HttpErrorInterceptor } from './errors/http-error-interceptor';
import { ErrorDialogComponent } from './errors/errordialog/error-dialog.component';

// Top-level modules

import { SharedModule } from './shared.module';
import { MapModule } from './map/map.module';
import { ReplicationModule } from './replication/replication.module';
import { UpgradeModule } from './upgrade/upgrade.module';

// Services

import { AuthenticationService } from './authentication/service/authentication.service';
import { TokenWatcherService } from './authentication/service/token-watcher.service';
import { CredentialService } from './authentication/credential.service';
import { ApplicationService } from './application/application.service';
import { BirdService } from './bird/bird.service';
import { WeightService } from './bird/bird-view/weight/weight.service';
import { ChartClickHandlerService } from './bird/bird-view/weight/chart-click-handler.service';
import { OptionsService } from './common/options.service';
import { InputService } from './common/input.service';
import { RouterUtilsService } from './common/router-utils.service';
import { SearchCacheService } from './common/searchcache.service';
import { ExportService } from './common/export.service';
import { FeedOutService } from './feedout/feedout.service';
import { ErrorsService } from './errors/errors.service';
import { TransmitterService } from './transmitter/transmitter.service';
import { SnarkImportService } from './snarkimport/snarkimport.service';
import { ReportLocationService } from './report/report-location/report-location.service';
import { ReportChecksheetService } from './report/report-checksheet/report-checksheet.service';
import { ReportMatingService } from './report/report-mating/report-mating.service';
import { ReportLatestWeightService } from './report/report-latest-weight/report-latest-weight.service';
import { ReportNoraNetErrorService } from './report/report-noranet-error/report-noranet-error.service';
import { RevisionService } from './common/revision.service';
import { SampleService } from './sample/sample.service';
import { NoraNetService } from './noranet/noranet.service';
import { FledgedChickService } from './nestobservation/fledgedchick.service';
import { WeanedChickService } from './nestobservation/weanedchick.service';
import { DeadEmbryoService } from './nestobservation/deadembryo.service';
import { HatchService } from './nestobservation/hatch.service';
import { LocationService } from './location/location.service';
import { NestObservationService } from './nestobservation/nestobservation.service';
import { NewEggService } from './nestobservation/newegg.service';
import { FertileEggService } from './nestobservation/fertileegg.service';
import { InfertileEggService } from './nestobservation/infertileegg.service';
import { PersonService } from './person/person.service';
import { ValidationService } from './form/validation.service';
import { FormService } from './form/form.service';
import { IndexRebuildService } from './application/index-rebuild.service';
import { LifeStageService } from './bird/lifestage/lifestage.service';
import { RecordService } from './record/record.service';
import { IslandService } from './setting/island/island.service';
import { SettingService } from './setting/setting.service';
import { SnarkActivityService } from './snarkactivity/snarkactivity.service';
import { TxMortalityService } from './setting/txmortality/txmortality.service';
import { OptionListService } from './setting/optionlist/option-list.service';
import { ReplicationService } from './replication/replication.service';

import { AppRoutingModule } from './app.routes';
import { AppComponent } from './app.component';

// This is the ngx-bootstrap way of defining the Locale, which is different to the Angular way (see below)
// Note that the 'en-gb' is ALL in lower case (different to the Angular way of 'en-GB')
defineLocale('en-gb', enGbLocale);

const myTokenGetter = () => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser'));
  return currentUser ? currentUser.token : '';
};

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    LoginComponent,
    PageNotFoundComponent,
    HomeComponent,
    ErrorDialogComponent,
    IndexRebuildComponent,
    SelectRecordTypeComponent,
  ],
  imports: [
    ReplicationModule,
    UpgradeModule,
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule,
    AlertModule.forRoot(),
    BsDropdownModule.forRoot(),
    BsDatepickerModule.forRoot(),
    ButtonsModule.forRoot(),
    ModalModule.forRoot(),
    CollapseModule.forRoot(),
    NgProgressModule,
    NgProgressHttpModule,
    PaginationModule.forRoot(),
    TabsModule.forRoot(),
    TypeaheadModule.forRoot(),
    ProgressbarModule.forRoot(),
    JwtModule.forRoot({
      config: {
        tokenGetter: myTokenGetter,
      },
    }),

    AppRoutingModule,

    FontAwesomeModule,
    SharedModule,
    MapModule,
  ],
  providers: [
    ApplicationService,
    AuthenticationService,
    AuthGuard,
    BirdService,
    LocationService,
    CredentialService,
    ExportService,
    FormService,
    OptionsService,
    OptionListService,
    InputService,
    IslandService,
    LifeStageService,
    PersonService,
    ReplicationService,
    RevisionService,
    RecordService,
    TransmitterService,
    TokenWatcherService,
    ValidationService,
    WeightService,
    FeedOutService,
    SampleService,
    TxMortalityService,
    ReportLocationService,
    SnarkActivityService,
    SnarkImportService,
    ReportChecksheetService,
    ErrorsService,
    SettingService,
    NestObservationService,
    NewEggService,
    FertileEggService,
    InfertileEggService,
    HatchService,
    DeadEmbryoService,
    FledgedChickService,
    WeanedChickService,
    ReportMatingService,
    ReportLatestWeightService,
    ReportNoraNetErrorService,
    IndexRebuildService,
    RouterUtilsService,
    ChartClickHandlerService,
    SearchCacheService,
    NoraNetService,
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true },
    {
      provide: ErrorHandler,
      useValue: Sentry.createErrorHandler({
        showDialog: false,
      }),
    },
    //    { provide: ErrorHandler, useClass: GlobalErrorHandler },

    // This is the Angular way of defining the Locale, but doesn't seem to to anything, so leaving it out for now.
    // Note: that the Angular definition of the locale is 'en-GB' different to the ngx-bootstrap way of 'en-gb'
    //{ provide: LOCALE_ID, useValue: 'en-GB' }
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor(library: FaIconLibrary, faConfig: FaConfig) {
    faConfig.fixedWidth = true;
    faConfig.defaultPrefix = 'fas';
    library.addIconPacks(fas);
  }
}
