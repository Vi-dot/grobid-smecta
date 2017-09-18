package org.grobid.service.data.model;

import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kohsuke.args4j.spi.ExplicitBooleanOptionHandler;
import org.kohsuke.args4j.Option;

@XmlRootElement
public class TrainingParams extends Model {
	
	@Option(name="-be", usage="Break epsilon")
	@XmlElement public double epsilon = 0.000001;
	@Option(name="-bw", usage="Break window")
	@XmlElement public int window = 20;
	@Option(name="-bm", usage="Break nbMaxIterations")
	@XmlElement public int nbMaxIterations = 0;
	
	@Option(name="-e-ratio", usage="Evaluation splitRatio")
	@XmlElement public double splitRatio = -1;
	@Option(name="-e-random", usage="Evaluation splitRandom", handler=ExplicitBooleanOptionHandler.class)
	@XmlElement public boolean splitRandom = false;
	
	@Option(name="-t-class", usage="Trainer class (e.g. \"org.grobid.trainer.AstroTrainer\"), can also be set in properties file.")
	@XmlElement public String trainerClass = "";
	
	public JsonObjectBuilder toJson() {
		JsonObjectBuilder builder = super.toJson();
		builder.add("epsilon", epsilon);
		builder.add("window", window);
		builder.add("nbMaxIterations", nbMaxIterations);
		builder.add("splitRatio", splitRandom);
		builder.add("splitRandom", splitRandom);
		return builder;
	}
	
	public String toCommand() {
		String command = "";
		command += "-be "+epsilon;
		command += " -bw "+window;
		command += " -bm "+nbMaxIterations;
		command += " -e-ratio "+splitRatio;
		command += " -e-random "+splitRandom;
		if ((trainerClass != null) && !trainerClass.isEmpty())
			command += "-t-class "+trainerClass;
		return command;
	}
}
