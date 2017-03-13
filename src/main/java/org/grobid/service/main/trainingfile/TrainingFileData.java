package org.grobid.service.main.trainingfile;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.grobid.core.utilities.AstroProperties;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import org.grobid.service.data.Data;
import org.grobid.service.data.DataId;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.utils.Utils;

public class TrainingFileData extends Data {

	@XmlRootElement
	public static class MetaDef {
		@XmlElement public DataId _id = new DataId();
		@XmlElement public int id = -1;
		@XmlElement public int state = 0;
		@XmlElement public boolean enable = true;
		@XmlElement public String name = "";
	}
	
	@XmlRootElement
	public static class ContentDef {
		@XmlElement public String filename;
		@XmlElement public String tei;
	}
	
	private MongoCollection<Document> mTrainingFileCollection;
	
	public TrainingFileData() {
		super();
		mTrainingFileCollection = db.getCollection("trainingFileMeta");
	}
	
	public JsonArray getAllMetaJson() throws IOException {
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
		
		JsonArray jsonArrayTrain = getAllMetaJsonFromDir(AstroProperties.get("grobid.astro.corpusPath"), true);
		Iterator<JsonValue> it = jsonArrayTrain.iterator();
		while (it.hasNext())
			jsonBuilder.add(it.next());
		
		JsonArray jsonArrayTrash = getAllMetaJsonFromDir(SmectaProperties.get("grobid.smecta.trainingFiles.trashDirectory"), false);
		it = jsonArrayTrash.iterator();
		while (it.hasNext())
			jsonBuilder.add(it.next());
		
		return jsonBuilder.build();
	}
	
	public JsonArray getAllMetaJsonFromDir(String dirPath, boolean defaultEnable) throws IOException {
		File dir = new File(dirPath);
		
		if (!dir.exists())
			throw new IOException("Can't find training files dir : "+dir.getAbsolutePath());
		
		if (!dir.isDirectory())
			throw new IOException("It's not a directory : "+dir.getAbsolutePath());
		
		String[] filesName = dir.list();
		
		Arrays.sort(filesName, Collections.reverseOrder());
		
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
		
		for (int i=0 ; i<filesName.length ; i++) {
			if (filesName[i].endsWith(".tei")) {
				MongoCursor<Document> it = mTrainingFileCollection.find(new BasicDBObject("name", filesName[i])).iterator();
				Document trainingFile = null;
				
				if (it.hasNext()) {
					trainingFile = it.next();
				}
				else {
					trainingFile = new Document();
					trainingFile.append("id", String.valueOf(mTrainingFileCollection.count()));
					trainingFile.append("createDate", Utils.currentDateISO());
					trainingFile.append("state", 0);
					trainingFile.append("enable", defaultEnable);
					trainingFile.append("name", filesName[i]);
					mTrainingFileCollection.insertOne(trainingFile);
				}
				it.close();
				
				JsonObject jsonObject = Json.createReader(new StringReader(trainingFile.toJson())).readObject();
				jsonBuilder.add(jsonObject);
			}
		}
		
		return jsonBuilder.build();
	}
	
	public boolean updateMeta(MetaDef def) {
		if (def.id == -1)
			return false;
		
		BasicDBObject setDocument = new BasicDBObject().append("$set",
				new BasicDBObject()
				.append("state", def.state)
				.append("enable", def.enable)
				.append("name", def.name)
		);
		
		UpdateResult result = mTrainingFileCollection.updateOne(Filters.eq("_id", new ObjectId(def._id.$oid)), setDocument);
		
		return (result.getModifiedCount() > 0);
	}
	
	public static void checkAndMoveFiles() throws Exception {
		TrainingFileData trainingFileData = new TrainingFileData();
		
		JsonArray files = trainingFileData.getAllMetaJson();
		
		Iterator<JsonValue> it = files.iterator();
		
		while (it.hasNext()) {
			JsonObject fileObj = (JsonObject)it.next();
			String fileName = fileObj.getString("name");
			
			String trainPath = AstroProperties.get("grobid.astro.corpusPath")+"/"+fileName;
			String trashPath = SmectaProperties.get("grobid.smecta.trainingFiles.trashDirectory")+"/"+fileName;
			
			File trainFile = new File(trainPath);
			File trashFile = new File(trashPath);
			
			if (fileObj.getBoolean("enable")) {
				if (!trainFile.exists() && trashFile.exists()) {
					Files.move(trashFile.toPath(), trainFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Move training file (to training) : "+fileName);
				}
				else if (!trainFile.exists())
					System.out.println("Move WARNING missing file (in training) : "+fileName);
			}
			else {
				if (trainFile.exists() && !trashFile.exists()) {
					Files.move(trainFile.toPath(), trashFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Move training file (to trash) : "+fileName);
				}
				else if (!trashFile.exists())
					System.out.println("Move WARNING missing file (in trash) : "+fileName);
			}
		}
	}
}
