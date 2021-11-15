import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthenticationRequest } from './authentication-request';
import { AuthenticationResponse } from './authentication-response';
import { JwtHelperService } from '@auth0/angular-jwt';
import { User } from './user';
import { LoginRequestEvent } from './login-request-event';
import { TokenWatcherService } from './token-watcher.service';
import { map, shareReplay } from 'rxjs/operators';

@Injectable()
export class AuthenticationService {
  private currentUserSource = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSource.asObservable();

  private loginRequestSource = new BehaviorSubject<any>(null);
  public loginRequest$ = this.loginRequestSource.asObservable();

  private loginEventSource = new BehaviorSubject<any>(null);
  public loginEvent$ = this.loginEventSource.asObservable();

  private logoutRequestSource = new BehaviorSubject<any>(null);
  public logoutRequest$ = this.logoutRequestSource.asObservable();

  /** Observable that lists the permissions. */
  private permissions$ = null;

  constructor(
    private http: HttpClient,
    private tokenWatcherService: TokenWatcherService,
    private jwtHelperService: JwtHelperService
  ) {
    if (tokenWatcherService.isTokenValid()) {
      // get user if saved in local storage
      const nextUser = JSON.parse(localStorage.getItem('currentUser'));

      // Note: although we're loading the authorities from an untrusted source
      // hacking these to give yourself elevated privileges still won't achieve
      // very much because it's the token that sent through to the server to get
      // the data and that can't be tampered with because it's digitally signed.
      this.setCurrentUser(nextUser);
    }

    this.tokenWatcherService.logoutEvent.subscribe(() => {
      console.log('AuthenticationService got token logout event');
      this.logout('Watcher');
    });

    this.tokenWatcherService.sessionExpiredEvent.subscribe(() => {
      console.log('AuthenticationService got token expired event');
      this.logout('Expired');
    });
  }

  setCurrentUser(currentUser: any) {
    if (!currentUser) {
      currentUser = new User();
    }
    this.currentUserSource.next(currentUser);
  }

  startLogin(targetUrl: string, optionalMessage?: string) {
    this.loginRequestSource.next(
      new LoginRequestEvent(targetUrl, optionalMessage)
    );
  }

  login(username: string, password: string): Observable<boolean> {
    const authenticationRequest = new AuthenticationRequest(username, password);
    return this.http.post('/api/authenticate', authenticationRequest).pipe(
      map((authenticationResponse: AuthenticationResponse) => {
        // login successful if there's a jwt token in the response
        if (authenticationResponse) {
          // set token property
          const token = authenticationResponse.token;
          const decodedToken = this.jwtHelperService.decodeToken(token);
          const authorities = decodedToken.authorities.split(',');

          const currentUser = new User();
          currentUser.name = decodedToken.name;
          currentUser.personId = decodedToken.personId;
          currentUser.token = token;
          currentUser.authorities = authorities;
          this.setCurrentUser(currentUser);

          console.log('CurrentUser = ' + JSON.stringify(currentUser));

          // store username and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(currentUser));

          this.permissions$ = this.permissions$ = this.http
            .get<string[]>('/api/permissions')
            .pipe(shareReplay());

          // Fire an event if login is good.
          this.loginEventSource.next('Login');

          // return true to indicate successful login
          return true;
        } else {
          // return false to indicate failed login
          return false;
        }
      })
    );
  }

  onUserLogout(): void {
    // clear token remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    this.setCurrentUser(null);
    this.permissions$ = of(new Array<string[]>());
    this.logout('User');
  }

  logout(logoutType: string): void {
    this.logoutRequestSource.next('Logout' + logoutType);
  }

  isLoggedIn() {
    const currentUserNow = this.currentUserSource.getValue();
    return currentUserNow && currentUserNow.token;
  }

  /**
   * Get the list of permissions for the current user as an observable. This creates the
   * observable if not already created. The observable is shared and replayed to prevent
   * multiple calls to the server.
   *
   * @returns
   */
  getPermissions(): Observable<string[]> {
    if (!this.permissions$) {
      this.permissions$ = this.http
        .get<string[]>('/api/permissions')
        .pipe(shareReplay());
    }
    return this.permissions$;
  }
}
