import { Component, Input, OnInit } from '@angular/core';
import { SettingService } from './setting.service';
import { Subscription, timer } from 'rxjs';

const STARRED_PASSWORD = '************';

@Component({
  selector: 'app-password-toggle',
  templateUrl: 'password-toggle.component.html',
})
export class PasswordToggleComponent implements OnInit {
  @Input() passwordType: string;
  @Input() label: string;

  password: string = STARRED_PASSWORD;

  isShowing = false;

  hideTimerSubscription: Subscription;

  constructor(private settingService: SettingService) {}

  ngOnInit(): void {}

  hide(): void {
    this.isShowing = false;
    this.password = STARRED_PASSWORD;
  }

  toggle() {
    if (!this.isShowing) {
      this.settingService.getPassword(this.passwordType).subscribe((value) => {
        this.password = value.password;
        this.isShowing = true;

        // Automatically collapse and remove after 10 seconds.
        this.hideTimerSubscription = timer(10000).subscribe(() => {
          this.hide();
        });
      });
    } else {
      if (this.hideTimerSubscription) {
        this.hideTimerSubscription.unsubscribe();
      }
      this.hide();
    }
  }
}
