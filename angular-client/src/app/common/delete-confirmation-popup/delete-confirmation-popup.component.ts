import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
  Input,
} from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap/modal';

@Component({
  selector: 'app-delete-confirmation-popup',
  templateUrl: './delete-confirmation-popup.component.html',
})
export class DeleteConfirmationPopupComponent implements OnInit {
  @ViewChild('deleteConfirmationModal', { static: true })
  public deleteConfirmationModal: ModalDirective;

  @Input()
  deleteOk = false;

  @Input()
  entityName: string;

  @Input()
  message: string;

  @Output()
  confirmation = new EventEmitter();

  constructor() {}

  ngOnInit() {}

  show() {
    this.deleteConfirmationModal.show();
  }

  onConfirmDelete() {
    this.confirmation.emit(true);
    this.deleteConfirmationModal.hide();
  }

  onCancelDelete() {
    this.deleteConfirmationModal.hide();
  }
}
