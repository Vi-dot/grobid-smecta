import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable()
export class TrainingFilesListSelectorService {

  private eventSourceFileSelected = new Subject();
  private eventObservableFileSelected = this.eventSourceFileSelected.asObservable();

  constructor() { }

  public onFileSelected(callback: (filename: string) => void) {
    this.eventObservableFileSelected.subscribe(callback);
  }

  public emitFileSelected(filename: string) {
    this.eventSourceFileSelected.next(filename);
  }

}
