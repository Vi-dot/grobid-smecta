import { Component, OnInit } from '@angular/core';
import {SimpleDialogService} from "../../tools/simple-dialog/simple-dialog.service";
import {Subscription} from "rxjs/Subscription";
import {TimerObservable} from "rxjs/observable/TimerObservable";
import {Http} from "@angular/http";

@Component({
  selector: 'new-training',
  templateUrl: './new-training.component.html',
  styleUrls: ['./new-training.component.scss']
})
export class NewTrainingComponent implements OnInit {

  public params: any = {
    'epsilon': 0.000001,
    'window': 20,
    'nbMaxIterations': 0,
    'splitRatio': 80,
    'splitRandom': false
  };
  ready: boolean = false;
  training: boolean = false;
  logs: string = "";
  reloader: Subscription = null;
  evaluation: boolean = true;

  constructor(private simpleDialogService: SimpleDialogService, private http: Http) { }

  ngOnInit() {
    this.updateState(true);
  }

  ngOnDestroy() {
    if (this.reloader)
      this.reloader.unsubscribe();
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

  public showHelp() {
    this.simpleDialogService.show({
      type: 'ok',
      message: `These 3 parameters define stopping conditions for training.<br>
      If <strong>nbMaxIterations</strong> is greater than 0, iterations don't exceed this value.<br>
      In any case, if there are lesser differences between objects, it stops.<br>
      It occurs on a period of iterations (<strong>window</strong>), when obj differential value didn't exceed <strong>epsilon</strong>.`
    });
  }

}
