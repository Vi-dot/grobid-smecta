import { Component, OnInit } from '@angular/core';
import {Subscription} from "rxjs";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import {Http} from "@angular/http";
import {TrainerService} from "./trainer.service";

@Component({
  selector: 'trainer',
  templateUrl: 'trainer.component.html',
  styleUrls: ['trainer.component.scss']
})
export class TrainerComponent implements OnInit {

  help: boolean = false;
  ready: boolean = false;
  training: boolean = false;
  logs: string = "";
  reloader: Subscription = null;
  evaluation: boolean = true;
  params: any = {
    'epsilon': 0.000001,
    'window': 20,
    'nbMaxIterations': 0,
    'splitRatio': 80,
    'splitRandom': false
  };

  constructor(private http: Http, private trainerService: TrainerService) {}

  ngOnInit() {
    this.updateState(true);
  }

  toggle() {
    this.ready = false;

    var params = Object.assign({},this.params); // clone

    if (this.evaluation)
      params.splitRatio /= 100;
    else
      params.splitRatio = -1;

    this.http.post('/api/trainer/toggle', params)
      .subscribe
      (data => {

        let res = data.json();
        console.log(res);
        this.updateState();

        if (this.reloader)
          this.reloader.unsubscribe();

        this.reloader = TimerObservable.create(500, 500).subscribe(t => {
          this.updateState();
        });

      }, error => {

        console.error(error);

      });
  }

  updateState(first: boolean = false) {

    this.http.get('/api/trainer/currentState')
      .subscribe
      (data => {

        let previousLogsLen: number = this.logs.length;

        let res = data.json();
        this.training = res.training;
        this.logs = res.logs;
        this.ready = true;

        var consoleDiv = document.getElementsByClassName("console")[0];
        if (this.logs.length > previousLogsLen)
          consoleDiv.scrollTop = consoleDiv.scrollHeight;

        if (!res.training && this.reloader) {
          this.reloader.unsubscribe();
          this.trainerService.emitTrained();
        }
        else if (first) {
          this.reloader = TimerObservable.create(500, 500).subscribe(t => {
            this.updateState();
          });
        }

      }, error => {

        console.error(error);

      });
  }
}
