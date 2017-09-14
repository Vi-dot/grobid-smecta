package org.grobid.service.main;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.grobid.service.data.Dao;
import org.grobid.service.data.model.TrainingFile;
import org.grobid.service.data.model.TrainingParams;
import org.grobid.service.main.trainer.TrainerService;
import org.grobid.service.main.trainingfile.TrainingFileService;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("api")
public class SmectaService {

	protected TrainerService mTrainerService;
	protected TrainingFileService mTrainingFileService;
	
	public SmectaService() {
		Dao.openDb();
		mTrainerService = new TrainerService();
		mTrainingFileService = new TrainingFileService();
	}
	
	@POST
	@Path("trainer/toggle")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response toggle(final TrainingParams params) {
		return mTrainerService.toggle(params);
	}
	
	@GET
	@Path("trainer/currentState")
	@Produces(MediaType.APPLICATION_JSON)
	public Response currentState() {
		return mTrainerService.currentState();
	}
	
	@GET
	@Path("trainer/data")
	@Produces(MediaType.APPLICATION_JSON)
	public Response data() {
		return mTrainerService.data();
	}
	
	@POST
	@Path("trainer/clearData")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response clearData() {
		return mTrainerService.clearData();
	}
	
	@GET
	@Path("training/meta-files")
	@Produces(MediaType.APPLICATION_JSON)
	public Response metaFiles() {
		return mTrainingFileService.metaFiles();
	}
	
	@POST
	@Path("training/meta-file")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postMetaFile(final TrainingFile params) {
		return mTrainingFileService.postMetaFile(params);
	}
	
	@GET
	@Path("training/file")
	@Produces(MediaType.APPLICATION_JSON)
	public Response file(@QueryParam("name") String filename) {
		return mTrainingFileService.file(filename);
	}
	
	@POST
	@Path("training/file")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFile(final TrainingFile params) {
		return mTrainingFileService.postFile(params);
	}
}
