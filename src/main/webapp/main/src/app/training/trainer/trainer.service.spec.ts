/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TrainerService } from './trainer.service';

describe('TrainerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TrainerService]
    });
  });

  it('should ...', inject([TrainerService], (service: TrainerService) => {
    expect(service).toBeTruthy();
  }));
});
