import { BrowserModule } from '@angular/platform-browser';
import {NgModule, LOCALE_ID} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';
import {Ng2PageScrollModule} from 'ng2-page-scroll';

import { AppComponent } from './app.component';
import { TrainerComponent } from './training/trainer/trainer.component';
import { TopComponent } from './top/top.component';
import { HomeComponent } from './home/home.component';
import {MaterialModule} from "@angular/material";
import { FlexLayoutModule } from '@angular/flex-layout';
import { TrainingResultsComponent } from './training/training-results/training-results.component';
import { TrainingComponent } from './training/training.component';
import {TrainerService} from "./training/trainer/trainer.service";
import { TimePipe } from './tools/time.pipe';
import { TrainingFilesComponent } from './training-files/training-files.component';
import { TrainingFilesListComponent } from './training-files/training-files-list/training-files-list.component';
import { TrainingFileEditorComponent } from './training-files/training-file-editor/training-file-editor.component';
import { TrainingFilesListSelectorService } from './training-files/training-files-list-selector/training-files-list-selector.service';
import { TrainingFilesListSelectorComponent } from './training-files/training-files-list-selector/training-files-list-selector.component';
import { SimpleDialog } from './tools/simple-dialog/simple-dialog.component';

const appRoutes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'trainer', component: TrainingComponent },
  { path: 'training-files', component: TrainingFilesComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];

@NgModule({
  declarations: [
    AppComponent,
    TrainerComponent,
    TopComponent,
    HomeComponent,
    TrainingResultsComponent,
    TrainingComponent,
    TimePipe,
    TrainingFilesComponent,
    TrainingFilesListComponent,
    TrainingFileEditorComponent,
    TrainingFilesListSelectorComponent,
    SimpleDialog
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    RouterModule.forRoot(appRoutes),
    MaterialModule.forRoot(),
    FlexLayoutModule.forRoot(),
    Ng2PageScrollModule.forRoot()
  ],
  entryComponents: [
    SimpleDialog
  ],
  providers: [
    { provide: LOCALE_ID, useValue: "fr-FR" },
    TrainerService,
    TrainingFilesListSelectorService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
