package org.grobid.service.data.model;

import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@XmlRootElement
public class TrainingParams extends Model {
	
	@XmlElement public double epsilon = 0.000001;
	@XmlElement public int window = 20;
	@XmlElement public int nbMaxIterations = 0;
	
	@XmlElement public double splitRatio = -1;
	@XmlElement public boolean splitRandom = false;
	
	public JsonObjectBuilder toJson() {
		JsonObjectBuilder builder = super.toJson();
		builder.add("epsilon", epsilon);
		builder.add("window", window);
		builder.add("nbMaxIterations", nbMaxIterations);
		builder.add("splitRatio", splitRandom);
		builder.add("splitRandom", splitRandom);
		return builder;
	}
}
