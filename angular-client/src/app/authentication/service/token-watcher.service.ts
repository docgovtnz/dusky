import { EventEmitter, Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable()
export class TokenWatcherService {
  loginEvent = new EventEmitter();
  sessionExpiredEvent = new EventEmitter();
  logoutEvent = new EventEmitter();
  tokenWatcherInterval: any = null;

  private currentTokenState: string = null; // null, Login, Expired, Logout

  constructor(private jwtHelper: JwtHelperService) {
    this.startTokenWatcher();
  }

  isTokenValid() {
    const currentToken = this.jwtHelper.tokenGetter();
    return (
      currentToken &&
      currentToken.length > 0 &&
      !this.jwtHelper.isTokenExpired(currentToken, 30)
    );
  }

  isTokenPresent() {
    const currentToken = this.jwtHelper.tokenGetter();
    return currentToken && currentToken.length > 0;
  }

  isTokenExpired() {
    return (
      this.isTokenPresent() &&
      this.jwtHelper.isTokenExpired(this.jwtHelper.tokenGetter(), 30)
    );
  }

  startTokenWatcher() {
    console.log('startTokenWatcher()');
    if (this.tokenWatcherInterval === null) {
      this.tokenWatcherInterval = setInterval(() => {
        let nextTokenState = null;
        if (this.isTokenValid()) {
          nextTokenState = 'Login';
        } else if (this.isTokenExpired()) {
          nextTokenState = 'Expired';
        } else {
          nextTokenState = 'Logout';
        }

        if (this.currentTokenState !== nextTokenState) {
          switch (nextTokenState) {
            case 'Login':
              this.loginEvent.next('Login');
              break;
            case 'Expired':
              this.sessionExpiredEvent.next('Expired');
              break;
            case 'Logout':
              this.logoutEvent.next('Logout');
              break;
            default:
              throw new Error('Unknown token state of ' + nextTokenState);
          }
        }

        this.currentTokenState = nextTokenState;
      }, 2000);
    }
  }
}
