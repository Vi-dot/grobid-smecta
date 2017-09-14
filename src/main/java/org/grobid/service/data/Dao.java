package org.grobid.service.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.grobid.core.utils.SmectaProperties;
import org.grobid.service.data.model.Model;

public abstract class Dao<M extends Model> {
	
	/*
	 * DB Location
	 */
	
	private static String getDirectory() {
		String dataDirectoryPath = SmectaProperties.get("grobid.smecta.dataDirectory");
		
		File dataDirectory = new File(dataDirectoryPath);
		if (!dataDirectory.exists())
			if (dataDirectory.mkdirs())
				dataDirectory.mkdir();
		
		return dataDirectoryPath;
	}
	
	
	public static Nitrite db = null;
	
	public static void openDb() {
		
		NitriteBuilder builder = Nitrite.builder()
				.filePath(getDirectory() + "/grobid-smecta.db");
		
		db = builder.openOrCreate();
	}
	
	/*
	 * DAO FACTORY
	 */
	
	protected static HashMap<String, Dao<?>> instances = new HashMap<String, Dao<?>>();
	
	public static <D extends Dao<?>> D getInstance(Class<D> daoClass) {
		
		@SuppressWarnings("unchecked")
		D instance = (D) instances.get(daoClass.getName());
		
		if (instance == null) {
			instance = newInstance(daoClass);
		}
		
		return instance;
	}
	
	private static synchronized <D extends Dao<?>> D newInstance(Class<D> daoClass) {
		try {
			
			@SuppressWarnings("unchecked")
			D instance = (D) Class.forName(daoClass.getName()).newInstance();
			instances.put(daoClass.getName(), instance);
			return instance;
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * DAO Object
	 */
	
	protected ObjectRepository<M> repo;
	
	public Dao(Class<M> modelClass) {		
		repo = db.getRepository(modelClass);
	}
	
	
	public M getOne(String id) {
		return repo.find(ObjectFilters.eq("id", id)).firstOrDefault();
	}
	
	public List<M> getAll() {
		return repo.find().toList();
	}
	
	public WriteResult insert(M model) {
		model.generateId();
		return repo.insert(model);
	}
	
}
