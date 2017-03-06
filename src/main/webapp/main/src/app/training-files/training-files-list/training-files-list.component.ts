import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {Http} from "@angular/http";
import {TrainingFilesListSelectorService} from "../training-files-list-selector/training-files-list-selector.service";

interface Stats {
  [ index: string ]: any
}

@Component({
  selector: 'training-files-list',
  templateUrl: './training-files-list.component.html',
  styleUrls: ['./training-files-list.component.scss']
})
export class TrainingFilesListComponent implements OnInit {

  currentIndex: number = -1;
  journaux = ['A&A', 'AJ', 'ApJS', 'ApJ', 'MNRAS', 'NewA'];
  stats: Stats = {};
  initStats() {
    for (var journal of this.journaux) {
      this.stats[journal] = {
        validNb: 0,
        total: 0
      };
    }
  }

  statsKeys() : Array<string> {
    return Object.keys(this.stats);
  }

  @Input('files')
  set setFiles(value) {
    this.files = value;
    this.initStats();

    for (var i=0 ; i<this.files.length ; i++) {
      var file = this.files[i];
      for (var j=0 ; j<this.statsKeys().length ; j++) {
        var statKey = this.statsKeys()[j];

        if (file.name.includes(statKey)) {
          if (file.enable) {
            this.stats[statKey].total++;
            if (file.state == 2)
              this.stats[statKey].validNb++;
          }
          break;
        }
      }
    }
  }
  files: any = [];

  @Input('editable')
  editable: boolean = false;

  @Output('onEdit') editEmitter: EventEmitter<any> = new EventEmitter<any>();
  update(file: any) {
    if (this.editable)
      this.editEmitter.next({'file': file});
  }

  @Input('clickable')
  clickable: boolean = false;

  @Output('onClick') clickEmitter: EventEmitter<any> = new EventEmitter<any>();
  select(file: any, index: number) {
    this.currentIndex = index;
    if (this.clickable)
      this.clickEmitter.next({'file': file});
  }

  constructor() {
    this.initStats();
  }

  ngOnInit() {

  }

}
