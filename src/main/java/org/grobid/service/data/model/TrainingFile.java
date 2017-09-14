package org.grobid.service.data.model;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;
import org.grobid.service.utils.Utils;

@XmlRootElement
public class TrainingFile extends Model {
	
	public Date createDate;
	
	//meta
	@XmlElement public int state = 0;
	@XmlElement public boolean enable = true;
	@XmlElement public String name = "";
	
	//content
	@XmlElement public String filename = "";
	@XmlElement public String tei = "";
	
	public JsonObjectBuilder toJson() {
		JsonObjectBuilder builder = super.toJson();
		builder.add("createDate", Utils.dateToISO(createDate));
		builder.add("state", state);
		builder.add("enable", enable);
		builder.add("name", name);
		builder.add("filename", filename);
		builder.add("tei", tei);
		return builder;
	}
}
