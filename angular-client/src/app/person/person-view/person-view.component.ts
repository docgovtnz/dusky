import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PersonEntity } from '../../domain/person.entity';
import { PersonService } from '../person.service';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';
import { AuthenticationService } from '../../authentication/service/authentication.service';

@Component({
  selector: 'app-person-view',
  templateUrl: 'person-view.component.html',
})
export class PersonViewComponent implements OnInit {
  personEntity: PersonEntity;
  deleteCheck: DeleteByIdCheckDto;

  // the current revision displayed (may be the current entity or an older revision)
  revision: PersonEntity;

  resetPasswordCheck = false;

  @ViewChild('resetPasswordComponent', { static: true })
  resetPasswordComponent: ResetPasswordComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: PersonService,
    private authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this.personEntity = new PersonEntity();
    this.service.findById(this.route.snapshot.params.id).subscribe((entity) => {
      this.personEntity = entity;
      // start by using the current person as the current revision
      this.revision = entity;
      this.loadDeleteCheck();

      this.authenticationService.getPermissions().subscribe((perms) => {
        if (perms.includes('PASSWORD_RESET')) {
          this.resetPasswordCheck = true;
        }
      });

      this.authenticationService.currentUser$.subscribe((username) => {
        if (username.personId === this.personEntity.id) {
          this.resetPasswordCheck = true;
        }
      });
    });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.personEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/person/edit/' + this.personEntity.id]);
  }

  onDelete() {
    this.service.delete(this.personEntity.id).subscribe(() => {
      this.router.navigate(['/person']);
    });
  }

  onResetPassword() {
    this.resetPasswordComponent.startReset(this.personEntity.userName);
  }

  onNewPassword() {
    this.resetPasswordComponent.startNewPassword(this.personEntity.userName);
  }

  onResetPasswordEvent() {
    this.router.navigate(['/person/' + this.personEntity.id]);
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }

  onLockAccountEvent() {
    this.router.navigate(['/person/' + this.personEntity.id]);
  }
}
