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

  @Input('files')
  set setFiles(value) {
    this.files = value;

    for (var i=0 ; i<this.files.length ; i++) {
      var file = this.files[i];
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
  }

  ngOnInit() {

  }

}
