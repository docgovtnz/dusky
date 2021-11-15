import { switchMap } from 'rxjs/operators';

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

import { BirdEntity } from '../../domain/bird.entity';
import { BirdService } from '../bird.service';
import { DeleteByIdCheckDto } from '../../domain/response/delete-by-id-check-dto';

@Component({
  selector: 'app-bird-view',
  templateUrl: 'bird-view.component.html',
})
export class BirdViewComponent implements OnInit {
  isCollapsed = false;

  birdEntity: BirdEntity;
  deleteCheck: DeleteByIdCheckDto;

  txActive = false;
  chipActive = false;
  bandActive = false;

  // the current revision displayed (may be the current entity or an older revision)
  revision: BirdEntity;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: BirdService
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap((params: ParamMap) => {
          switch (params.get('idSearch')) {
            case 'Transmitters':
              this.txActive = true;
              break;
            case 'Chips':
              this.chipActive = true;
              console.log(params.get('idSearch'));
              break;
            case 'Bands':
              this.bandActive = true;
              break;
            default:
              break;
          }
          return this.service.findById(params.get('id'));
        })
      )
      .subscribe((bird) => {
        this.birdEntity = bird;
        // start by using the current bird as the current revision
        this.revision = bird;
        this.loadDeleteCheck();
      });
  }

  loadDeleteCheck() {
    this.service
      .deleteByIdCheck(this.birdEntity.id)
      .subscribe((deleteCheck) => (this.deleteCheck = deleteCheck));
  }

  isDeleteOk() {
    return this.deleteCheck && this.deleteCheck.deleteOk;
  }

  onEdit() {
    this.router.navigate(['/bird/edit/' + this.birdEntity.id]);
  }

  onDelete() {
    this.service.delete(this.birdEntity.id).subscribe(() => {
      this.router.navigate(['/bird']);
    });
  }

  onRevisionEvent(revision) {
    this.revision = revision;
  }
}
