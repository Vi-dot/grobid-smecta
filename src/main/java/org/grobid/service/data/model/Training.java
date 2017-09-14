package org.grobid.service.data.model;

import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;
import org.grobid.service.utils.Utils;

import com.fasterxml.jackson.core.JsonParser;

import org.dizitart.no2.IndexType;

@Indices({
    @Index(value = "createDate", type = IndexType.NonUnique)
})
public class Training extends Model {
	
	public Date createDate;
	
	public String logs;
	
	public TrainingParams params;
	
	public TrainingResults results;
	
	public List<TrainingFile> trainingFiles;
	
	public JsonObjectBuilder toJson() {
		JsonObjectBuilder builder = super.toJson();
		builder.add("createDate", Utils.dateToISO(createDate));
		builder.add("logs", logs);
		builder.add("params", params.toJson());
		builder.add("results", results.toJson());
		if (trainingFiles != null)
			builder.add("trainingFiles", listToJson(trainingFiles));
		return builder;
	}
}
