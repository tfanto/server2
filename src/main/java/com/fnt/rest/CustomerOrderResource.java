package com.fnt.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.entity.CustomerOrderHead;
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
	@Path("batch")
	public Response createCustomerOrderQueue(String customerOrderJson) {
		service.post(customerOrderJson);
		return Response.ok().build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("header")
	public Response createCustomerOrderHeader(String customerOrderHeadJson) {
		CustomerOrderHead obj = service.createHeader(customerOrderHeadJson);
		return Response.ok(obj).build();
	}

}
