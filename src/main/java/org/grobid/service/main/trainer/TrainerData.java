package org.grobid.service.main.trainer;

import java.io.StringReader;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import org.grobid.service.data.Data;
import org.grobid.service.data.model.Training;
import org.grobid.service.data.model.TrainingParams;
import org.grobid.service.data.model.TrainingResults;
import org.grobid.service.utils.Utils;

public class TrainerData extends Data {
	
	/*@XmlRootElement
	public static class ParamsDef {
		@XmlElement public double epsilon = 0.000001;
		@XmlElement public int window = 20;
		@XmlElement public int nbMaxIterations = 0;
		
		@XmlElement public double splitRatio = -1;
		@XmlElement public boolean splitRandom = false;
	}
	
	@XmlRootElement
	public static class ResultsDef {
		@XmlElement public int labelingTimeMs = 0;
		
		@XmlElement public int totalInstance = 0;
		@XmlElement public int correctInstance = 0;
		@XmlElement public double recallInstance = 0;
		
		@XmlElement public int executionTimeMs = 0;
	}*/
	
	//private MongoCollection<Document> mTrainerCollection;
	
	public void add(String logs, TrainingParams params, JsonArray trainingFiles, TrainingResults results) {
		/*Document document = new Document();
		document.append("id", String.valueOf(mTrainerCollection.count()));
		document.append("createDate", Utils.currentDateISO());
		document.append("logs", logs);
		
		BasicDBObject paramsObject = new BasicDBObject();
		paramsObject.append("epsilon", params.epsilon);
		paramsObject.append("window", params.window);
		paramsObject.append("nbMaxIterations", params.nbMaxIterations);
		paramsObject.append("splitRatio", params.splitRatio);
		paramsObject.append("splitRandom", params.splitRandom);
		document.append("params", paramsObject);
		
		document.append("trainingFiles", trainingFiles.toString());
		
		BasicDBObject resultsObject = new BasicDBObject();
		resultsObject.append("executionTimeMs", results.executionTimeMs);
		resultsObject.append("labelingTimeMs", results.labelingTimeMs);
		resultsObject.append("totalInstance", results.totalInstance);
		resultsObject.append("correctInstance", results.correctInstance);
		resultsObject.append("recallInstance", results.recallInstance);
		document.append("results", resultsObject);
		
		mTrainerCollection.insertOne(document);*/
		
		Training training = new Training();
		
		training.createDate = Utils.currentDateISO();
		training.logs = logs;
		
		training.params = params;
		db.insert(training.params);
		
		training.results = results;
		db.insert(training.results);
		
		db.insert(training);
	}
	
	public String getAllJson() {
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
		
		List<Training> trainings = Data.getAll(Training.class);
		
		for (Training training : trainings)
			jsonBuilder.add(training.toJson());
		
		return jsonBuilder.build().toString();
	}
	
	public void clearAll() {
	}
}
