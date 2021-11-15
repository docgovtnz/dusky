import { Component, Input, OnInit } from '@angular/core';
import { BirdService } from '../bird.service';
import { OptionsService } from '../../common/options.service';
import { LifeStageEntity } from '../../domain/lifestage.entity';
import { LifeStageService } from '../lifestage/lifestage.service';

@Component({
  selector: 'app-bird-age-class-label',
  templateUrl: './bird-age-class-label.component.html',
  styleUrls: ['./bird-age-class-label.component.scss'],
})
export class BirdAgeClassLabelComponent implements OnInit {
  ageClass: string = null;
  pastRevision = false;

  private _birdID: string;

  // TODO populate from OptionsService.getOptions('AgeClasses')
  private ageClasses = ['Unknown', 'Egg', 'Chick', 'Juvenile', 'Adult'];

  constructor(
    private birdService: BirdService,
    private lifeStageService: LifeStageService,
    private optionsService: OptionsService
  ) {}

  ngOnInit() {}

  get birdID(): string {
    return this._birdID;
  }

  @Input()
  set birdID(value: string) {
    this.ageClass = null;
    this._birdID = value;

    this.pastRevision = value && value.indexOf(':') > -1;

    console.log(
      'BirdAgeClass Label - birdID = ' +
        this._birdID +
        ' revision = ' +
        this.pastRevision
    );

    this.loadAgeClass();
  }

  loadAgeClass() {
    if (this.birdID) {
      this.birdService.getAgeClass(this.birdID).subscribe((ageClass) => {
        this.ageClass = ageClass;
      });
    }
  }

  onChangeAgeClass(direction: number) {
    if (this.ageClass) {
      const currentAgeClassIdx = this.ageClasses.indexOf(this.ageClass);
      const lastIndex = this.ageClasses.length - 1;
      if (currentAgeClassIdx >= 1 && currentAgeClassIdx <= lastIndex) {
        const newAgeClassIdx = this.clamp(
          currentAgeClassIdx + direction,
          1,
          lastIndex
        );
        const newAgeClass = this.ageClasses[newAgeClassIdx];

        if (this.ageClass !== newAgeClass) {
          this.updateAgeClass(newAgeClass);
        }
      }
    }
  }

  clamp(value: number, min: number, max: number): number {
    return value <= min ? min : value >= max ? max : value;
  }

  updateAgeClass(newAgeClass: string) {
    const lifeStage = new LifeStageEntity();
    lifeStage.birdID = this.birdID;
    lifeStage.dateTime = new Date();
    lifeStage.ageClass = newAgeClass;
    lifeStage.changeType = 'Manual';
    this.lifeStageService.save(lifeStage).subscribe(() => {
      console.log('Lifestage updated to ' + newAgeClass);
      this.ageClass = null;
      this.loadAgeClass();
    });
  }
}
