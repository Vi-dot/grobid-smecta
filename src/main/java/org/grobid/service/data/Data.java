package org.grobid.service.data;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import org.grobid.core.utils.SmectaProperties;

public class Data {

	public static MongoDatabase db = null;
	
	public static void init() {		
		
		MongoClient mongoClient = new MongoClient(
				SmectaProperties.get("grobid.smecta.mongodb.host"),
				Integer.parseInt(SmectaProperties.get("grobid.smecta.mongodb.port"))
		);
		db = mongoClient.getDatabase("grobid-smecta-db");
	}
	
	public Data() {
		if (db == null)
			init();
	}
	
}
