import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import * as Sentry from '@sentry/angular';
import { BsLocaleService } from 'ngx-bootstrap/datepicker';

import { LoginComponent } from './authentication/login/login.component';
import { AuthenticationService } from './authentication/service/authentication.service';
import { ApplicationService } from './application/application.service';
import { ApplicationStatus } from './application/application-status';
import { OptionsService } from './common/options.service';
import { SettingService } from './setting/setting.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit, AfterViewInit {
  @ViewChild('loginComponent', { static: true })
  loginComponent: LoginComponent;

  applicationStatus: ApplicationStatus;
  helpLink: string;

  currentUserName: string;
  private _displayMenuItems = false;

  constructor(
    public router: Router,
    private applicationService: ApplicationService,
    private authenticationService: AuthenticationService,
    private localeService: BsLocaleService,
    private optionsService: OptionsService,
    public settingService: SettingService
  ) {}

  ngOnInit(): void {
    this.localeService.use('en-gb');
    console.log('Using Locale: ' + this.localeService.currentLocale);

    this.applicationService.getStatus().subscribe((applicationStatus) => {
      this.applicationStatus = applicationStatus;

      Sentry.configureScope((scope) => {
        scope.addEventProcessor((event) => {
          event.environment = applicationStatus.environment;
          event.release = applicationStatus.version;
          return event;
        });
      });
    });

    this.authenticationService.currentUser$.subscribe((currentUser) => {
      if (currentUser && currentUser.name) {
        this._displayMenuItems = true;
        this.currentUserName = currentUser.name;
      }
    });

    this.settingService
      .getHelpLink()
      .subscribe((helpLink) => (this.helpLink = helpLink));
  }

  ngAfterViewInit(): void {
    this.authenticationService.loginRequest$.subscribe((loginRequestEvent) => {
      this.loginComponent.startLogin(loginRequestEvent);
    });

    this.authenticationService.logoutRequest$.subscribe(
      (logoutRequestEvent) => {
        if (logoutRequestEvent) {
          switch (logoutRequestEvent) {
            case 'LogoutExpired':
              this.authenticationService.startLogin(
                null,
                'Session expired, please login again.'
              );
              break;
            case 'LogoutUser':
            case 'LogoutWatcher':
              this._displayMenuItems = false;
              this.optionsService.stopCaches();
              this.router.navigate(['home']);

              this.applicationService.getStatus().subscribe((status) => {
                if (
                  this.applicationStatus &&
                  this.applicationStatus.version !== status.version
                ) {
                  // reload the application
                  location.reload();
                }
              });

              break;
            default:
              throw new Error('Unknown type of logout: ' + logoutRequestEvent);
          }
        }
      }
    );
  }

  /**
   * We have our own isDisplayMenuItems() function independently of the AuthenticationService so that any state
   * changes funnel through our subscription to the currentUser rather than the internal state of the
   * service.
   */
  isDisplayMenuItems() {
    return this._displayMenuItems;
  }

  isLoginLinkVisible() {
    return !this.isDisplayMenuItems();
  }

  isLogoutLinkVisible() {
    return this.isDisplayMenuItems();
  }

  onLogin() {
    this.authenticationService.startLogin('/dashboard');
  }

  onLogout() {
    this.authenticationService.onUserLogout();
  }

  onNewItem(item): void {
    this.router.navigate(['/' + item + '/edit/-1'], {
      queryParams: { newEntity: true },
    });
  }

  onClickLogo(): void {
    const redirect = this.isDisplayMenuItems() ? '/dashboard' : '/';
    this.router.navigate([redirect]);
  }
}
