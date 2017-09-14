package org.grobid.service.main.trainingfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.grobid.service.data.Dao;
import org.grobid.service.data.model.TrainingFile;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.grobid.core.utils.SmectaProperties;

public class TrainingFileDao extends Dao<TrainingFile> {
	
	public TrainingFileDao() {
		super(TrainingFile.class);
	}
	
	public List<TrainingFile> getAllMeta() throws IOException {
		
		List<TrainingFile> res = getAllMetaFromDir(SmectaProperties.get("grobid.smecta.trainingFiles.mainDirectory"), true);
		res.addAll(getAllMetaFromDir(SmectaProperties.get("grobid.smecta.trainingFiles.trashDirectory"), false));
		
		return res;
	}
	
	public List<TrainingFile> getAllMetaFromDir(String dirPath, boolean defaultEnable) throws IOException {
		File dir = new File(dirPath);
		
		if (!dir.exists())
			throw new IOException("Can't find training files dir : "+dir.getAbsolutePath());
		
		if (!dir.isDirectory())
			throw new IOException("It's not a directory : "+dir.getAbsolutePath());
		
		String[] filesName = dir.list();
		
		Arrays.sort(filesName, Collections.reverseOrder());
		
		ArrayList<TrainingFile> res = new ArrayList<TrainingFile>();
		
		for (int i=0 ; i<filesName.length ; i++) {
			if (filesName[i].endsWith(".tei") || filesName[i].endsWith(".tei.xml")) {
				Iterator<TrainingFile> it = repo.find(ObjectFilters.eq("name", filesName[i])).iterator();
				TrainingFile trainingFile = null;
				
				if (it.hasNext()) {
					trainingFile = it.next();
				}
				else {
					trainingFile = new TrainingFile();
					trainingFile.createDate = new Date();
					trainingFile.state = 0;
					trainingFile.enable = defaultEnable;
					trainingFile.name = filesName[i];
					this.insert(trainingFile);
				}
				
				res.add(trainingFile);
			}
		}
		
		return res;
	}
	
	public boolean updateMeta(TrainingFile newDef) {
		if (newDef.id == null)
			return false;
		
		TrainingFile trainingFile = this.getOne(newDef.id);
		
		trainingFile.state = newDef.state;
		trainingFile.enable = newDef.enable;
		trainingFile.name = newDef.name;
		
		WriteResult result = repo.update(trainingFile);
		
		return (result.getAffectedCount() > 0);
	}
	
	public static void checkAndMoveFiles() throws Exception {
		TrainingFileDao trainingFileDao = Dao.getInstance(TrainingFileDao.class);
		
		List<TrainingFile> files = trainingFileDao.getAllMeta();
		
		Iterator<TrainingFile> it = files.iterator();
		
		while (it.hasNext()) {
			TrainingFile fileObj = (TrainingFile)it.next();
			String fileName = fileObj.name;
			
			String trainPath = SmectaProperties.get("grobid.smecta.trainingFiles.mainDirectory")+"/"+fileName;
			String trashPath = SmectaProperties.get("grobid.smecta.trainingFiles.trashDirectory")+"/"+fileName;
			
			File trainFile = new File(trainPath);
			File trashFile = new File(trashPath);
			
			if (fileObj.enable) {
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
