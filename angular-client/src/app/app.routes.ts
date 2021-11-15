// eslint-disable-next-line @typescript-eslint/quotes
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PageNotFoundComponent } from './application/page-not-found.component';
import { HomeComponent } from './application/home/home.component';
import { AuthGuard } from './authentication/auth.guard';
import { DashboardComponent } from './application/dashboard/dashboard.component';
import { EsriMapComponent } from './map/esri-map.component';
import { LocationMapComponent } from './map/location-map/location-map.component';
import { RecordMapComponent } from './map/record-map/record-map.component';
import { ClusterViewComponent } from './replication/cluster-view/cluster-view.component';
import { UpgradeComponent } from './upgrade/upgrade.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
  },

  {
    path: 'map',
    component: EsriMapComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'map/Location',
    component: LocationMapComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'map/Record',
    component: RecordMapComponent,
    canActivate: [AuthGuard],
  },

  {
    path: 'bird',
    loadChildren: () => import('./bird/bird.module').then((m) => m.BirdModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'cluster-view',
    component: ClusterViewComponent,
    canActivate: [AuthGuard],
  },

  {
    path: 'idsearch',
    loadChildren: () =>
      import('./identification/identification.module').then(
        (m) => m.IdentificationModule
      ),
    canActivate: [AuthGuard],
  },

  {
    path: 'location',
    loadChildren: () =>
      import('./location/location.module').then((m) => m.LocationModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'noranet',
    loadChildren: () =>
      import('./noranet/noranet.module').then((m) => m.NoraNetModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'person',
    loadChildren: () =>
      import('./person/person.module').then((m) => m.PersonModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'record',
    loadChildren: () =>
      import('./record/record.module').then((m) => m.RecordModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'transmitter',
    loadChildren: () =>
      import('./transmitter/transmitter.module').then(
        (m) => m.TransmitterModule
      ),
    canActivate: [AuthGuard],
  },

  {
    path: 'settings',
    loadChildren: () =>
      import('./setting/setting.module').then((m) => m.SettingModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'snarkactivity',
    loadChildren: () =>
      import('./snarkactivity/snarkactivity.module').then(
        (m) => m.SnarkActivityModule
      ),
    canActivate: [AuthGuard],
  },

  {
    path: 'snarkimport',
    loadChildren: () =>
      import('./snarkimport/snarkimport.module').then(
        (m) => m.SnarkImportModule
      ),
    canActivate: [AuthGuard],
  },

  {
    path: 'feedout',
    loadChildren: () =>
      import('./feedout/feedout.module').then((m) => m.FeedOutModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'report',
    loadChildren: () =>
      import('./report/report.module').then((m) => m.ReportModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'nestobservation',
    loadChildren: () =>
      import('./nestobservation/nestobservation.module').then(
        (m) => m.NestObservationModule
      ),
    canActivate: [AuthGuard],
  },

  {
    path: 'sample',
    loadChildren: () =>
      import('./sample/sample.module').then((m) => m.SampleModule),
    canActivate: [AuthGuard],
  },

  {
    path: 'upgrade',
    component: UpgradeComponent,
    canActivate: [AuthGuard],
  },

  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
