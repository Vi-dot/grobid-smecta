package org.grobid.service.main.trainer;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.grobid.trainer.AbstractTrainer;
import org.grobid.service.data.Dao;
import org.grobid.service.data.model.TrainingParams;
import org.grobid.service.data.model.TrainingResults;
import org.grobid.service.main.trainingfile.TrainingFileDao;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.utils.Utils;

public class TrainerService {

	protected class TrainRunnable implements Runnable {
		
		public void run() {
			
			try {
				TrainingFileDao.checkAndMoveFiles();
			
			
				TrainerRunner.process(mParams);
				
		    	saveData();
		    	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		    	
		    mRunning = false;
	    }
	}
	
	protected boolean mRunning;
	protected Thread mTrainerThread;
	protected TrainerDao mTrainerDao;
	protected TrainingParams mParams;
	
	public TrainerService() {		
		mRunning = false;
		mTrainerThread = null;
		mTrainerDao = Dao.getInstance(TrainerDao.class);
	}
	
	public Response toggle(final TrainingParams params) {
		
		if (!mRunning) {
			mRunning = true;
			mParams = params;
			
			mTrainerThread = new Thread(new TrainRunnable());
			mTrainerThread.start();
			
	        JsonObject resJson = Json.createObjectBuilder()
					.add("message", "Trainer started.")
					.build();
			
			Response response = Response
	                .ok()
	                .type(MediaType.APPLICATION_JSON)
	                .entity(resJson.toString())
	                .build();
			
			return response;
		}
		else {
			if (mTrainerThread != null)
				mTrainerThread.interrupt();
			
	        JsonObject resJson = Json.createObjectBuilder()
					.add("message", "Trainer stoped.")
					.build();
			
			Response response = Response
	                .ok()
	                .type(MediaType.APPLICATION_JSON)
	                .entity(resJson.toString())
	                .build();
			
			return response;
		}
		
	}
	
	public Response currentState() {
		
		String logs = getCurrentLogs();
		
		JsonObject resJson = Json.createObjectBuilder()
				.add("training", mRunning)
				.add("logs", logs)
				.build();
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(resJson.toString())
                .build();
		
		return response;
	}
	
	public String getCurrentLogs() {
		String logs = "";
		String logFile = SmectaProperties.get("grobid.smecta.trainer.log");
		try {
			logs = FileUtils.readFileToString(new File(logFile));
		} catch (IOException e) {
			logs = e.getMessage();
		}
		
		return logs;
	}
	
	public void saveData() {		
		String logs = getCurrentLogs();
		
		TrainingResults results = new TrainingResults();
		
		try {
			results.executionTimeMs = Integer.parseInt(Utils.findValueInText(logs, "Total execution time: ", " ms"));
			results.labelingTimeMs = Integer.parseInt(Utils.findValueInText(logs, "Labeling took: ", " ms"));
			
			results.totalInstance = Integer.parseInt(Utils.findValueInText(logs, Utils.addSpaces("Total expected instances:", 28), "\n"));
			results.correctInstance = Integer.parseInt(Utils.findValueInText(logs, Utils.addSpaces("Correct instances:", 28), "\n"));
			results.recallInstance = Double.parseDouble(Utils.findValueInText(logs, Utils.addSpaces("Instance-level recall:", 28), "\n").replace(",","."));
		}
		catch(Exception e) {
			System.out.println("WARNING : parse error on logs to get results.");
		}
    	
    	try {
			mTrainerDao.insert(logs, mParams, Dao.getInstance(TrainingFileDao.class).getAllMeta(), results);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Response data() {
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(mTrainerDao.getAllJson())
                .build();
		
		return response;
	}
	
	public Response clearData() {
		
		mTrainerDao.clearAll();
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity("{}")
                .build();
		
		return response;
	}
}
