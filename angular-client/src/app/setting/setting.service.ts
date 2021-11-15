import { Observable, timer } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { SettingBaseService } from './setting-base.service';
import { Response } from '../domain/response/response';
import { shareReplay } from 'rxjs/operators';
import { SettingEntity } from '../domain/setting.entity';
import { PasswordDTO } from './password-dto';
import { SettingCriteria } from './setting.criteria';

import { User } from '../authentication/service/user';
import { AuthenticationService } from '../authentication/service/authentication.service';
/** Refresh interval set to one minute (in milliseconds). */
const REFRESH_INTERVAL = 1000 * 60;
const ADMIN_ROLE = 'System Admin';

@Injectable()
export class SettingService extends SettingBaseService {
  private cache$: Observable<any>;
  private currentUser: User;

  public settingCriteria: SettingCriteria;

  constructor(
    http: HttpClient,
    private authenticationService: AuthenticationService
  ) {
    super(http);
    // we just simply clear the cache every refresh interval and let it be refreshed by the next getAll call
    timer(0, REFRESH_INTERVAL).subscribe(() => (this.cache$ = null));
    this.authenticationService.currentUser$.subscribe(
      (currentUser) => (this.currentUser = currentUser)
    );
  }

  public save(entity: SettingEntity): Observable<Response<SettingEntity>> {
    // clear cache so next getAll returns most recent values
    this.cache$ = null;
    return super.save(entity);
  }

  public delete(docId: string): Observable<any> {
    // clear cache so next getAll returns most recent values
    this.cache$ = null;
    return super.delete(docId);
  }

  public get(ids: string[]): Observable<any> {
    // const params = new HttpParams();
    // ids.forEach((id) => params.append('id', id));
    return this.http.get<any>('/api/setting', { params: { id: ids } });
  }

  public put(settings: any): Observable<Response<any>> {
    // clear cache so next getAll returns most recent values
    this.cache$ = null;
    return this.http.put<Response<any>>('/api/setting', settings);
  }

  public getAll(): Observable<any> {
    // using first method from blog post https://blog.thoughtram.io/angular/2018/03/05/advanced-caching-with-rxjs.html
    // so that we only go and get the defaults once
    if (!this.cache$) {
      this.cache$ = this.http.get<any>('/api/setting/all').pipe(shareReplay(1));
    }
    return this.cache$;
  }

  public getPassword(passwordType: string): Observable<PasswordDTO> {
    return this.http.get<PasswordDTO>('/api/setting/password/' + passwordType);
  }

  public getHelpLink(): Observable<string> {
    return this.http.get('/api/setting/helpLink', { responseType: 'text' });
  }

  public isAuthorizedToAdd(): boolean {
    const roles = this.currentUser.authorities;
    //console.log(roles);
    for (let i = 0; i < roles.length; i++) {
      if (roles[i].toLowerCase() === ADMIN_ROLE.toLowerCase()) {
return true;
}
    }
    return false;
  }
}
