import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { AuthenticationService } from '../service/authentication.service';
import { Router } from '@angular/router';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { LoginRequestEvent } from '../service/login-request-event';
import { TokenWatcherService } from '../service/token-watcher.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  @ViewChild('loginModal', { static: true })
  loginModal: ModalDirective;

  @Output()
  loginEvent = new EventEmitter();

  targetUrl: string;
  eventMessage: string;
  model: any = {};
  loading = false;
  error: string;
  loginStarted = false;

  constructor(
    private router: Router,
    private tokenWatcherService: TokenWatcherService,
    private authenticationService: AuthenticationService
  ) {}

  ngOnInit() {
    this.tokenWatcherService.loginEvent.subscribe((loginEvent) => {
      if (loginEvent && this.loginStarted) {
        this.endLogin();
      }
    });

    this.authenticationService.logoutRequest$.subscribe(
      (logoutRequestEvent) => {
        if (logoutRequestEvent) {
          switch (logoutRequestEvent) {
            case 'LogoutExpired':
              // ignore
              break;
            case 'LogoutWatcher':
              // In a multi tab scenario if some other tab has done logout then we logout too
              this.onCancelLogin();
              break;
            case 'LogoutUser':
              // ignore
              break;
            default:
              throw new Error('Unknown type of logout: ' + logoutRequestEvent);
          }
        }
      }
    );
  }

  startLogin(loginRequestEvent: LoginRequestEvent) {
    if (!this.loginStarted && loginRequestEvent) {
      this.loginStarted = true;
      this.loading = false;
      this.error = null;
      this.model = {};

      this.targetUrl = loginRequestEvent.targetUrl; // can be null (login appears above the logged in current page)
      this.eventMessage = loginRequestEvent.optionalMessage;

      // DON'T do this anymore, each tab is able to watch the status of the token
      // always reset any currently active login
      //this.authenticationService.logout("Quiet");

      this.loginModal.show();
    }
  }

  login() {
    this.loading = true;
    this.authenticationService
      .login(this.model.username, this.model.password)
      .subscribe(
        (response) => {
          this.loading = false;

          if (response) {
            this.endLogin();
          } else {
            this.error = 'Username or password is incorrect';
          }
        },
        () => {
          this.loading = false;
          this.error = 'Username or password is incorrect';
        }
      );
  }

  endLogin() {
    this.loginStarted = false;
    this.loginModal.hide();

    if (this.targetUrl) {
      // This is a bit of a hack but seems to be the best solution available at the moment.
      // https://stackoverflow.com/questions/39396075/how-to-reload-the-component-of-same-url-in-angular-2
      this.router.navigateByUrl('/DummyUrl').then(() => {
        this.router.navigateByUrl(this.targetUrl);
      });
    }
  }

  onCancelLogin() {
    this.loginStarted = false;
    this.loginModal.hide();
    this.authenticationService.onUserLogout();
  }
}
