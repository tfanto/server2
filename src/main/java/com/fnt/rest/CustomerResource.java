package com.fnt.rest;

import java.util.Base64;
import java.util.Base64.Decoder;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fnt.dto.SearchData;
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
	public Response delete(@PathParam("id") Long id) {
		service.delete(id);
		return Response.ok().build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	public Response get(@PathParam("id") Long id) {
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
	@Path(value = "paginatesearch")
	public Response paginatesearch(@QueryParam("offset") String offs, @QueryParam("limit") String lim, @QueryParam("customernumber") String customernumber, @QueryParam("name") String name, @QueryParam("sortorder") String sortorder) {

		Decoder decoder = Base64.getDecoder();
		String offsetStr = new String(decoder.decode(offs));
		String limitStr = new String(decoder.decode(lim));
		String customernumberStr = new String(decoder.decode(customernumber));
		String nameStr = new String(decoder.decode(name));
		String sortorderStr = new String(decoder.decode(sortorder));
		Integer offset = Integer.parseInt(offsetStr);
		Integer limit = Integer.parseInt(limitStr);
		List<Customer> items = service.paginatesearch(offset, limit, customernumberStr, nameStr, sortorderStr);
		return Response.ok(items).build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@Path("paginatecount")
	public Response paginatecount(@QueryParam("customernumber") String customernumber, @QueryParam("name") String name) {

		Decoder decoder = Base64.getDecoder();
		String customernumberStr = new String(decoder.decode(customernumber));
		String nameStr = new String(decoder.decode(name));
		Long items = service.paginatecount(customernumberStr, nameStr);
		return Response.ok(items).build();
	}

	
	

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@Path(value = "prompt")
	public Response prompt(@QueryParam("customernumber") String cn, @QueryParam("name") String n) {

		Decoder decoder = Base64.getDecoder();
		String customernumber = new String(decoder.decode(cn));
		String name = new String(decoder.decode(n));

		List<SearchData> rs = service.prompt(customernumber, name);

		return Response.ok(rs).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@Path("orderinginfo")
	public Response getAllCustomerNumbers() {
		List<Long> items = service.getAllCustomerIds();
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
