/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TrainingFilesListSelectorService } from './training-files-list-selector.service';

describe('TrainingFilesListSelectorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TrainingFilesListSelectorService]
    });
  });

  it('should ...', inject([TrainingFilesListSelectorService], (service: TrainingFilesListSelectorService) => {
    expect(service).toBeTruthy();
  }));
});
