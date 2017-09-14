package org.grobid.service.main.trainer;

import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import org.grobid.service.data.Dao;
import org.grobid.service.data.model.Training;
import org.grobid.service.data.model.TrainingParams;
import org.grobid.service.data.model.TrainingResults;
import org.grobid.service.data.model.TrainingFile;

public class TrainerDao extends Dao<Training> {

	public TrainerDao() {
		super(Training.class);
	}

	public void insert(String logs, TrainingParams params, List<TrainingFile> trainingFiles, TrainingResults results) {
		
		Training training = new Training();
		
		training.createDate = new Date();
		training.logs = logs;
		
		training.params = params;
		
		training.results = results;
		
		training.trainingFiles = trainingFiles;
		
		insert(training);
	}
	
	public String getAllJson() {
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
		
		List<Training> trainings = getAll();
		
		for (Training training : trainings)
			jsonBuilder.add(training.toJson().build());
		
		return jsonBuilder.build().toString();
	}
	
	public void clearAll() {
	}
}
