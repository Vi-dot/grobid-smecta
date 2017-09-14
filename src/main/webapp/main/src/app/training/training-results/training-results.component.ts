import { Component, OnInit } from '@angular/core';
import {Http} from "@angular/http";
import {Observable} from "rxjs";
import {MdTabChangeEvent} from "@angular/material";

@Component({
  selector: 'training-results',
  templateUrl: 'training-results.component.html',
  styleUrls: ['training-results.component.scss']
})
export class TrainingResultsComponent implements OnInit {

  selectedItem: any;
  data: any = [];
  showTrainingFiles: boolean = false;

  constructor(private http: Http) {
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.http.get('/api/trainer/data')
      .subscribe
      (data => {

        var temp = data.json();

        this.data = temp;

        console.log(this.data);

      }, error => {

        console.error(error);

      });
  }

  clearData() {
    this.http.post('/api/trainer/clearData', {})
      .subscribe
      (data => {

        this.refresh();

      }, error => {

        console.error(error);

      });
  }

  onTabFocused($event: any) {
    if ($event.tab.textLabel == 'Training Files') {
      setTimeout(() => this.showTrainingFiles = true, 500);
    }
    else {
      this.showTrainingFiles = false;
    }
  }

}
