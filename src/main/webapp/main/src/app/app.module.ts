import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, LOCALE_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule, Routes } from '@angular/router';
import { Ng2PageScrollModule } from 'ng2-page-scroll';

import { AppComponent } from './app.component';
import { TopComponent } from './top/top.component';
import { HomeComponent } from './home/home.component';
import { MaterialModule } from "@angular/material";
import { FlexLayoutModule } from '@angular/flex-layout';
import { TrainingResultsComponent } from './training/training-results/training-results.component';
import { TimePipe } from './tools/time.pipe';
import { TrainingFilesComponent } from './files-editor/training-files.component';
import { TrainingFilesListComponent } from './files-editor/training-files-list/training-files-list.component';
import { TrainingFileEditorComponent } from './files-editor/training-file-editor/training-file-editor.component';
import { TrainingFilesListSelectorService } from './files-editor/training-files-list-selector/training-files-list-selector.service';
import { TrainingFilesListSelectorComponent } from './files-editor/training-files-list-selector/training-files-list-selector.component';
import { SimpleDialogComponent } from './tools/simple-dialog/simple-dialog.component';
import { NewTrainingComponent } from './training/new-training/new-training.component';
import { ListTrainingComponent } from './training/list-training/list-training.component';
import { SimpleDialogService } from "./tools/simple-dialog/simple-dialog.service";

const appRoutes: Routes = [
  { path: 'home', component: HomeComponent },
  {
    path: 'trainings',
    children: [
      { path: '', component: ListTrainingComponent },
      { path: 'new', component: NewTrainingComponent }
    ]
  },
  { path: 'files-editor', component: TrainingFilesComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];

@NgModule({
  declarations: [
    AppComponent,
    TopComponent,
    HomeComponent,
    TrainingResultsComponent,
    TimePipe,
    TrainingFilesComponent,
    TrainingFilesListComponent,
    TrainingFileEditorComponent,
    TrainingFilesListSelectorComponent,
    SimpleDialogComponent,
    NewTrainingComponent,
    ListTrainingComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpModule,
    RouterModule.forRoot(appRoutes),
    MaterialModule,
    FlexLayoutModule,
    Ng2PageScrollModule.forRoot()
  ],
  entryComponents: [
    SimpleDialogComponent
  ],
  providers: [
    { provide: LOCALE_ID, useValue: "fr-FR" },
    TrainingFilesListSelectorService,
    SimpleDialogService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
