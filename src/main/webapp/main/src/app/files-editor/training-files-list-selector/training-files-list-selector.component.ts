import { Component, OnInit } from '@angular/core';
import {Http} from "@angular/http";
import {TrainingFilesListSelectorService} from "./training-files-list-selector.service";

@Component({
  selector: 'training-files-list-selector',
  templateUrl: './training-files-list-selector.component.html',
  styleUrls: ['./training-files-list-selector.component.scss']
})
export class TrainingFilesListSelectorComponent implements OnInit {

  files: any = [];
  loading: boolean;

  constructor(private http: Http, private filesListService: TrainingFilesListSelectorService ) { }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.loading = true;
    this.http.get('/api/training/meta-files')
      .subscribe
      (data => {

        this.loading = false;
        this.files = data.json();

      }, error => {

        this.loading = false;
        console.error(error);

      });
  }

  select(filename: string) {
    this.filesListService.emitFileSelected(filename);
  }

  update(file) {
    this.loading = true;
    this.http.post('/api/training/meta-file', file)
      .subscribe
      (data => {

        this.loading = false;
        console.log(data.json());

      }, error => {

        this.loading = false;
        console.error(error);

      });
  }

}
