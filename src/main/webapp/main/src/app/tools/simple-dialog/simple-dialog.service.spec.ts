import { TestBed, inject } from '@angular/core/testing';

import { SimpleDialogService } from './simple-dialog.service';

describe('SimpleDialogService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SimpleDialogService]
    });
  });

  it('should be created', inject([SimpleDialogService], (service: SimpleDialogService) => {
    expect(service).toBeTruthy();
  }));
});
