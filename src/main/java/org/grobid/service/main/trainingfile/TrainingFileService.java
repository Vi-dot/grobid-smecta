package org.grobid.service.main.trainingfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.data.Dao;
import org.grobid.service.data.model.Model;
import org.grobid.service.data.model.TrainingFile;
import org.grobid.service.utils.ErrorHandler;

public class TrainingFileService {
	
	protected TrainingFileDao mTrainingFileDao;
	
	public TrainingFileService() {		
		mTrainingFileDao = Dao.getInstance(TrainingFileDao.class);
		createTrainingTrashIfNeeded();
	}
	
	private void createTrainingTrashIfNeeded() {
		File dir = new File(SmectaProperties.get("grobid.smecta.trainingFiles.trashDirectory"));
		if (!dir.exists()) {
			if (!dir.mkdirs())
				ErrorHandler.print("Can't create trainingTrash directory, check access rights.");
		}
	}
	
	public Response metaFiles() {
		
		Response response;
		try {
			
			response = Response
			        .ok()
			        .type(MediaType.APPLICATION_JSON)
			        .entity(Model.listToJson(mTrainingFileDao.getAllMeta()).build().toString())
			        .build();
			
		} catch (IOException e) {
			return ErrorHandler.print(e.getMessage());
		}
		
		return response;
	}
	
	public Response postMetaFile(TrainingFile metaDef) {
		
		boolean res = mTrainingFileDao.updateMeta(metaDef);
		
		if (!res)
			return ErrorHandler.print("File not found.");
			
		Response response = Response
	        .ok()
	        .type(MediaType.APPLICATION_JSON)
	        .entity("{\"updated\": \""+res+"\" }")
	        .build();
		
		return response;
	}
	
	public Response file(String filename) {
		
		File dir = new File(SmectaProperties.get("grobid.smecta.trainingFiles.mainDirectory"));
		
		if (!dir.exists())
			return ErrorHandler.print("Can't find training files dir : "+dir.getAbsolutePath());
		
		if (!dir.isDirectory())
			return ErrorHandler.print("It's not a directory : "+dir.getAbsolutePath());
		
		File file = new File(dir.getAbsolutePath() + "/" + filename);
		
		if (!file.exists())
			return ErrorHandler.print("Can't find training file : "+file.getAbsolutePath());
		
		String content = null;
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			content = new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JsonObject resJson = Json.createObjectBuilder()
				.add("content", content)
				.build();
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(resJson.toString())
                .build();
		
		return response;
	}
	
	
	
	public Response postFile(TrainingFile params) {
		
		File dir = new File(SmectaProperties.get("grobid.smecta.trainingFiles.mainDirectory"));
		
		File file = new File(dir.getAbsolutePath() + "/" + params.filename);
		
		if (!file.exists())
			return ErrorHandler.print("Can't find training file : "+file.getAbsolutePath());
		
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			return ErrorHandler.print("Can't create local file. Please check access rights for : "+file.getAbsolutePath());
		}
		
		try {
			outputStream.write(params.tei.getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Response response = Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity("{}")
                .build();
		
		return response;
	}

	
}
