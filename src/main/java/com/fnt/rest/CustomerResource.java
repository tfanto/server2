package com.fnt.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fnt.entity.Customer;
import com.fnt.service.CustomerService;

@Path("customer")
public class CustomerResource {

	@Inject
	private CustomerService service;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response create(Customer customer) {
		Customer created = service.create(customer);
		return Response.ok(created).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response update(Customer customer) {
		Customer updated = service.update(customer);
		return Response.ok(updated).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@RolesAllowed({ "ADMIN", "USER" })
	public Response delete(@PathParam("id") String id) {
		service.delete(id);
		return Response.ok().build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	public Response get(@PathParam("id") String id) {
		Customer fetched = service.get(id);
		if (fetched == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("Does not exist : " + id).build();
		} else {
			return Response.ok(fetched).build();
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	public Response getAll() {
		List<Customer> items = service.getAll();
		return Response.ok(items).build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@Path("ids")
	public Response getAllCustomerIds() {
		List<String> items = service.getAllCustomerIds();
		return Response.ok(items).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "all")
	@RolesAllowed({ "ADMIN" })
	public Response deleteAll() {
		int i = service.deleteAll();
		return Response.ok(i).build();
	}

}
