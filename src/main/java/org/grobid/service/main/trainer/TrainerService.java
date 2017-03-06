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
import org.grobid.core.exceptions.GrobidException;
import org.grobid.core.main.LibraryLoader;
import org.grobid.core.mock.MockContext;
import org.grobid.core.utilities.GrobidProperties;
import org.grobid.trainer.AbstractTrainer;
import org.grobid.trainer.AstroTrainer;

import org.grobid.service.main.trainer.TrainerData.ParamsDef;
import org.grobid.service.main.trainer.TrainerData.ResultsDef;
import org.grobid.service.main.trainingfile.TrainingFileData;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.utils.Utils;

public class TrainerService {

	protected class TrainRunnable implements Runnable {
		
		public void run() {
			
			String logPath = SmectaProperties.get("grobid.smecta.trainer.log");
			File logFile = new File(logPath);
	    	
			ProcessBuilder builder = null;
	    	if (mParams != null)
	    		builder = new ProcessBuilder("mvn", "generate-resources", "-e", "-Ptrain", "-Dexec.args="+String.valueOf(mParams.epsilon)+" "+String.valueOf(mParams.window)+" "+String.valueOf(mParams.nbMaxIterations)+" "+String.valueOf(mParams.splitRatio)+" "+String.valueOf(mParams.splitRandom));
	    	else
	    		builder = new ProcessBuilder("mvn", "generate-resources","-Ptrain");
	    	
	    	builder.redirectErrorStream(true);
	    	
	    	builder.redirectOutput(Redirect.to(logFile));
	    	Process process = null;
	    	try {
				process = builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    	try {
				int res = process.waitFor();
			} catch (InterruptedException e) {
				process.destroy();
			}
	    	
	    	saveData();
	    	
	    	mRunning = false;
	    }
	}
	
	protected boolean mRunning;
	protected Thread mTrainerThread;
	protected TrainerData mTrainerData;
	protected ParamsDef mParams;
	
	public TrainerService() {		
		mRunning = false;
		mTrainerThread = null;
		mTrainerData = new TrainerData();
	}
	
	public Response toggle(final ParamsDef params) {
		
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
		
		ResultsDef results = new ResultsDef();
		
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
		
		TrainingFileData trainingFileData = new TrainingFileData();
		JsonArray trainingFiles = null;
		try {
			trainingFiles = trainingFileData.getAllMetaJson();
		} catch (IOException e) {
			System.out.println("WARNING : can't get training files.");
		}
    	
    	mTrainerData.add(logs, mParams, trainingFiles, results);
	}
	
	public Response data() {
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(mTrainerData.getAllJson())
                .build();
		
		return response;
	}
	
	public Response clearData() {
		
		mTrainerData.clearAll();
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity("{}")
                .build();
		
		return response;
	}
	
	public static void main(String[] args) {
		String grobidHome = SmectaProperties.get("grobid.home");
		String grobidProperties = SmectaProperties.get("grobid.properties");

		try {
			MockContext.setInitialContext(grobidHome, grobidProperties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GrobidProperties.getInstance();

		LibraryLoader.load();
		
		try {
			TrainingFileData.checkAndMoveFiles();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		AstroTrainer trainer = null;
		double splitRatio = -1;
		boolean splitRandom = false;
		
		String[] argss;
		if (args.length == 1)
			argss = args[0].split(" ");
		else
			argss = args;
			
		if (argss.length > 3) {
			ParamsDef params = new ParamsDef();
			
			for (int i=0 ; i<argss.length ; i++) {
				switch (i) {
				case 0:
					params.epsilon = Double.parseDouble(argss[i]);
					break;
				case 1:
					params.window = Integer.parseInt(argss[i]);
					break;
				case 2:
					params.nbMaxIterations = Integer.parseInt(argss[i]);
					break;
				case 3:
					splitRatio = Double.parseDouble(argss[i]);
					break;
				case 4:
					splitRandom = Boolean.parseBoolean(argss[i]);
					break;
				}
			}
			
			System.out.println("Params:\nepsilon:"+params.epsilon+"\nwindow:"+params.window+"\nnbMaxIterations:"+params.nbMaxIterations+"\nsplitRatio:"+splitRatio+"\nsplitRandom:"+splitRandom);
			
			trainer = new AstroTrainer(params.epsilon, params.window, params.nbMaxIterations);
		}
		
		if (trainer == null)
			trainer = new AstroTrainer();
		
		if (splitRatio == -1)
			AbstractTrainer.runTraining(trainer);
		else
			runSplitTrainingEvaluation(trainer, splitRatio, splitRandom);
			//AbstractTrainer.runSplitTrainingEvaluation(trainer, splitRatio);
	}
	
	public static void runSplitTrainingEvaluation(final AstroTrainer trainer, Double split, boolean random) {
        long start = System.currentTimeMillis();
        try {
            String report = trainer.splitTrainEvaluate(split, random);
            System.out.println(report);
        } catch (Exception e) {
            throw new GrobidException("An exception occurred while evaluating Grobid.", e);
        }
        long end = System.currentTimeMillis();
        System.out.println("Total execution time: " + (end - start) + " ms");
    }
	
}
