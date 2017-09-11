import { Injectable } from '@angular/core';
import {MdDialog, MdDialogConfig, MdDialogRef} from "@angular/material";
import {SimpleDialogComponent} from "./simple-dialog.component";

@Injectable()
export class SimpleDialogService {

  constructor(public dialog: MdDialog) { }

  public show(data: any): MdDialogRef<SimpleDialogComponent> {
    let dialogConfig: MdDialogConfig = new MdDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.panelClass = 'glu';
    dialogConfig.data = data;
    return this.dialog.open(SimpleDialogComponent, dialogConfig);
  }
}
