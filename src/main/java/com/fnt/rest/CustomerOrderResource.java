package com.fnt.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.service.CustomerOrderService;

@Path("customerorder")

public class CustomerOrderResource {

	ObjectMapper MAPPER = null;

	@Inject
	private CustomerOrderService service;

	
	
	public CustomerOrderResource() {
		MAPPER = new ObjectMapper();
		MAPPER.registerModule(new JavaTimeModule());
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response createCustomerOrderQueue(String customerOrderJson) {
		try {
			service.post(customerOrderJson);
			return Response.ok().build();
		} catch (RuntimeException e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
