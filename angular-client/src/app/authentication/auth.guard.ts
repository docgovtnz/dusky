import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private jwtHelperService: JwtHelperService
  ) {}

  canActivate() {
    // Don't call isTokenExpired() on the client, let the server do it (client clock might be different to server's)
    //let canActivate = this.jwtHelperService.tokenGetter() && !this.jwtHelperService.isTokenExpired();

    // tokenGetter() isn't a function, it actually gets the token value
    const canActivate = this.jwtHelperService.tokenGetter() !== null;
    if (!canActivate) {
      console.log('AuthGuard: !canActivate so navigate to /home');
      // not logged in so redirect to home page
      this.router.navigate(['/home']);
    }

    return canActivate;
  }
}
