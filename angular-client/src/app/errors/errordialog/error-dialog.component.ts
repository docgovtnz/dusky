import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { ErrorsService } from '../errors.service';
import { ModalDirective } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
})
export class ErrorDialogComponent implements OnInit, AfterViewInit {
  errorMessage: string;
  errorDetails: string;

  isCollapsed = true;

  @ViewChild('errorModal', { static: true })
  public errorModal: ModalDirective;

  constructor(private errorsService: ErrorsService) {}

  ngOnInit() {}

  ngAfterViewInit() {
    console.log('Subscribing to error handler');
    this.errorsService.errors$.subscribe((error) => {
      if (error !== null) {
        this.errorMessage = error.errorMessage;
        this.errorDetails = error.errorDetails;
        this.errorModal.show();
      }
    });
  }

  onCloseErrorDialog(): void {
    this.errorModal.hide();
  }
}
