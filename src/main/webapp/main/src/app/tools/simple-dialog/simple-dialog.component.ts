import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from "@angular/material";

@Component({
  selector: 'simple-dialog',
  templateUrl: './simple-dialog.component.html',
  styleUrls: ['./simple-dialog.component.scss']
})
export class SimpleDialog {

  public message: string = 'No message';

  public yes: string = 'Yes';
  public no: string = 'No';

  constructor(public dialogRef: MdDialogRef<SimpleDialog>) {

  }

}
