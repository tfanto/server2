package com.fnt.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.fnt.entity.AppUser;
import com.fnt.service.AppUserService;

@Path("appuser")
public class AppUserResource {

	@Inject
	private AppUserService service;

	@Context
	private SecurityContext sc;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response update(AppUser user) {
		String loggedInUser = sc.getUserPrincipal().getName();
		AppUser updated = service.store(loggedInUser, user);
		return Response.ok(updated).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "{login}")
	public Response get(@PathParam("login") String login) {
		String loggedInUser = sc.getUserPrincipal().getName();
		AppUser fetched = service.get(loggedInUser, login);
		return Response.ok(fetched).build();
	}

}
