package org.grobid.service.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ErrorHandler {

	public static Response print(String message) {
		System.err.println("Error : "+message);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \""+message+"\"}").build();
	}
}
