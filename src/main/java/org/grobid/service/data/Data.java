package org.grobid.service.data;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import io.jsondb.JsonDBTemplate;

import java.io.File;
import java.util.List;

import javax.json.JsonObject;

import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.data.model.Model;
import org.grobid.service.data.model.Training;
import org.grobid.service.data.model.TrainingParams;
import org.grobid.service.data.model.TrainingResults;

public abstract class Data {

	//public static MongoDatabase db = null;
	public static JsonDBTemplate db = null;
	
	public static Class<?>[] COLLECTIONS = {
		Training.class
	};
	
	public static void init() {
		
		/*MongoClient mongoClient = new MongoClient(
				SmectaProperties.get("grobid.smecta.mongodb.host"),
				Integer.parseInt(SmectaProperties.get("grobid.smecta.mongodb.port"))
		);
		db = mongoClient.getDatabase("grobid-smecta-db");*/
		
		String dbFilesLocation = SmectaProperties.get("grobid.smecta.dataDirectory");
		File dbFilesDir = new File(dbFilesLocation);
		if (!dbFilesDir.exists())
			if (dbFilesDir.mkdirs())
				dbFilesDir.mkdir();
		
		String baseScanPackage = "org.grobid.service.data.model";
		db = new JsonDBTemplate(dbFilesLocation, baseScanPackage);
		
		for (Class<?> collectionClass : COLLECTIONS) {
			if (!db.collectionExists(collectionClass))
				db.createCollection(collectionClass);
		}
	}
	
	public Data() {
		if (db == null)
			init();
	}
	
	public static <M extends Model> List<M> getAll(Class<M> clazz) {
		List<M> entities = (List<M>)db.findAll(clazz);
		
		
		/*for (Data entity : entities)
			entity.populate();*/
		
		return entities;
	}
	
}
