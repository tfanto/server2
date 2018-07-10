package com.fnt.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fnt.dao.NumberSerieDao;
import com.fnt.entity.NumberSerie;

@Path("ns")
public class NumberSerieResource {

	@Inject
	private NumberSerieDao service;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response create(NumberSerie numberSerie) {
		NumberSerie created = service.create(numberSerie);
		return Response.ok(created).build();
	}

}