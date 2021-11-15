import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { IndexRebuildService } from '../index-rebuild.service';
import { IndexRebuildStatus } from '../index-rebuild-status';
import { timer } from 'rxjs/internal/observable/timer';
import { concatMap, switchMap, takeWhile } from 'rxjs/operators';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { of } from 'rxjs/internal/observable/of';

@Component({
  selector: 'app-index-rebuild',
  templateUrl: './index-rebuild.component.html',
})
export class IndexRebuildComponent implements OnInit {
  step: 'WARNING' | 'PROGRESS' | 'COMPLETE' = 'WARNING';

  status: IndexRebuildStatus;

  @ViewChild('resetIndexModal', { static: true })
  public resetIndexModal: ModalDirective;

  constructor(private indexResetService: IndexRebuildService) {}

  ngOnInit() {}

  show() {
    this.resetIndexModal.show();
  }

  begin() {
    this.indexResetService.resetIndexes().subscribe((status) => {
      this.step = 'PROGRESS';
      this.status = status;

      timer(0, 10000)
        .pipe(
          switchMap((_) => this.indexResetService.getResetStatus()),
          concatMap((status) =>
            status.status === 'Complete' ? of(status, null) : of(status)
          ),
          takeWhile((status) => status !== null)
        )
        .subscribe((status) => {
          this.status = status;
          if (this.status.status === 'Complete') {
            this.step = 'COMPLETE';
          }
        });
    });
  }

  close() {
    this.resetIndexModal.hide();
    if (this.status !== null && this.status.status === 'Complete') {
      this.step = 'WARNING';
    }
  }
}
