package org.grobid.service.data.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
    @Index(value = "id", type = IndexType.Unique)
})
public abstract class Model implements Serializable {
	
	@Id public String id = null;
	
	public void generateId() {
		this.id = UUID.randomUUID().toString();
	}
	
	public JsonObjectBuilder toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (id != null)
			builder.add("id", id);
		return builder;
	}
	
	public static <M extends Model> JsonArrayBuilder listToJson(List<M> list) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		for (M model : list) {
			builder.add(model.toJson());
		}
		
		return builder;
	}
}
