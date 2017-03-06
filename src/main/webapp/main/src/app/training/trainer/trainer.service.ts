import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable()
export class TrainerService {

  private eventSourceTrained = new Subject();
  private eventObservableTrained = this.eventSourceTrained.asObservable();

  constructor() { }

  public onTrained(callback: () => void) {
    this.eventObservableTrained.subscribe(callback);
  }

  public emitTrained() {
    this.eventSourceTrained.next();
  }
}
