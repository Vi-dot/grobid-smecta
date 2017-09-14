package org.grobid.service.data.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@XmlRootElement
public class TrainingResults extends Model {
	
	@XmlElement public int labelingTimeMs = 0;
	
	@XmlElement public int totalInstance = 0;
	@XmlElement public int correctInstance = 0;
	@XmlElement public double recallInstance = 0;
	
	@XmlElement public int executionTimeMs = 0;

	public JsonObjectBuilder toJson() {
		JsonObjectBuilder builder = super.toJson();
		builder.add("labelingTimeMs", labelingTimeMs);
		builder.add("totalInstance", totalInstance);
		builder.add("correctInstance", correctInstance);
		builder.add("recallInstance", recallInstance);
		builder.add("executionTimeMs", executionTimeMs);
		return builder;
	}
}
