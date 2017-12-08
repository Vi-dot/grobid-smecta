package org.grobid.service.main.trainer;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import org.grobid.core.main.LibraryLoader;
import org.grobid.core.utilities.GrobidProperties;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.data.model.TrainingParams;
import org.grobid.trainer.AbstractTrainer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class TrainerRunner {

	public static void process(TrainingParams params) throws IOException {
		
		File logFile = new File(SmectaProperties.get("grobid.smecta.trainer.log"));
		if (!logFile.exists()) {
			logFile.mkdirs();
			logFile.createNewFile();
		}
    	
		
		ArrayList<String> command = new ArrayList<String>();
		command.add("mvn"); command.add("generate-resources"); command.add("-e"); command.add("-Ptrain");
		
		if (params != null)
			command.add("-Dexec.args=" + params.toCommand());
		
		ProcessBuilder builder = new ProcessBuilder(command);
		
    	
    	builder.redirectErrorStream(true);
    	builder.redirectOutput(Redirect.to(logFile));
    	
    	
    	Process process = builder.start();
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			process.destroy();
		}
	}
	
	public static void main(String[] args) {
		try {
			
			initContext();
			
			TrainingParams params = parseParams(args);
			
			AbstractTrainer trainer = initTrainer(params);
			
			runTrainer(params, trainer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initContext() {
		try {
			String grobidHome = SmectaProperties.get("grobid.home");
			String grobidProperties = SmectaProperties.get("grobid.properties");

			GrobidProperties.getInstance();

			LibraryLoader.load();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private static TrainingParams parseParams(String[] args) {			
		TrainingParams params = new TrainingParams();
		
		CmdLineParser parser = new CmdLineParser(params);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
		}
		
		return params;
	}
	
	private static AbstractTrainer initTrainer(TrainingParams params) throws IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String trainerClass = params.trainerClass;
		
		if (trainerClass.isEmpty()) {
			trainerClass = SmectaProperties.get("grobid.semcta.trainerClass");
			
			if ((trainerClass == null) || (trainerClass.isEmpty()))
				throw new IllegalArgumentException("Trainer class is not set, check grobid-smecta.properties or -t-class option.");
		}
		
		@SuppressWarnings("unchecked")
		Class<? extends AbstractTrainer> clazz = (Class<? extends AbstractTrainer>)Class.forName(trainerClass);
		
		if (clazz != null)
			return clazz.newInstance();
		
		return null;
	}
	
	private static void runTrainer(TrainingParams params, AbstractTrainer trainer) throws Exception {
		
		trainer.setParams(params.epsilon, params.window, params.nbMaxIterations);
		
		// no Evaluation
		if (params.splitRatio == -1) {
			AbstractTrainer.runTraining(trainer);
		}
		// with Evaluation
		else {
			long start = System.currentTimeMillis();
			
			//String report = trainer.splitTrainEvaluate(params.splitRatio, params.splitRandom);
			String report = trainer.splitTrainEvaluate(params.splitRatio);
			System.out.println(report);
			
	        long end = System.currentTimeMillis();
	        System.out.println("Total execution time: " + (end - start) + " ms");
		}
	}
}
