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
import org.grobid.core.utilities.AstroProperties;

import org.grobid.service.main.trainingfile.TrainingFileData.ContentDef;
import org.grobid.service.main.trainingfile.TrainingFileData.MetaDef;
import org.grobid.service.utils.ErrorHandler;

public class TrainingFileService {
	
	protected TrainingFileData mTrainingFileData;
	
	public TrainingFileService() {		
		mTrainingFileData = new TrainingFileData();
	}
	
	public Response metaFiles() {
		
		Response response;
		try {
			
			response = Response
			        .ok()
			        .type(MediaType.APPLICATION_JSON)
			        .entity(mTrainingFileData.getAllMetaJson().toString())
			        .build();
			
		} catch (IOException e) {
			return ErrorHandler.print(e.getMessage());
		}
		
		return response;
	}
	
	public Response postMetaFile(MetaDef def) {
		
		boolean res = mTrainingFileData.updateMeta(def);
		
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
		
		File dir = new File(AstroProperties.get("grobid.astro.corpusPath"));
		
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
	
	
	
	public Response postFile(ContentDef params) {
		
		File dir = new File(AstroProperties.get("grobid.astro.corpusPath"));
		
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
