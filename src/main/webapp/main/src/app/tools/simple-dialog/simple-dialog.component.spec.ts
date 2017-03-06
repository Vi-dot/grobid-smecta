/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { SimpleDialog } from './simple-dialog.component';

describe('SimpleDialog', () => {
  let component: SimpleDialog;
  let fixture: ComponentFixture<SimpleDialog>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SimpleDialog ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SimpleDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
