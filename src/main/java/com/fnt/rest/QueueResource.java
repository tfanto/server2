package com.fnt.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fnt.message.AppJMSMessageBrowser;

@Path("queue")
public class QueueResource {

	@Inject
	private AppJMSMessageBrowser browser;

	@GET
	@Path("browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	public Response browsequeue() {
		browser.browseMessages();
		return Response.ok().build();
	}
}