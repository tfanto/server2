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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.fnt.dto.SearchData;
import com.fnt.entity.Item;
import com.fnt.entity.ItemView1;
import com.fnt.service.ItemService;

@Path("item")
public class ItemResource {

	@Inject
	private ItemService service;

	@Context
	private SecurityContext sc;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response create(Item item) {
		String userName = sc.getUserPrincipal().getName();
		item.setChangedby(userName);
		Item created = service.create(item);
		return Response.ok(created).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	public Response update(Item item) {
		String userName = sc.getUserPrincipal().getName();
		item.setChangedby(userName);
		Item updated = service.update(item);
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
	@RolesAllowed({ "ADMIN", "USER" })
	public Response get(@PathParam("id") Long id) {
		Item fetched = service.get(id);
		if (fetched == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(Entity.json("Does not exist : " + id)).build();
		} else {
			return Response.ok(fetched).build();
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("paginatesearch")
	public Response paginatesearch(@QueryParam("offset") String offs, @QueryParam("limit") String lim, @QueryParam("itemnumber") String itemnumber, @QueryParam("description") String description, @QueryParam("sortorder") String sortorder) {

		Decoder decoder = Base64.getDecoder();
		String offsetStr = new String(decoder.decode(offs));
		String limitStr = new String(decoder.decode(lim));
		String itemnumberStr = new String(decoder.decode(itemnumber));
		String descriptionStr = new String(decoder.decode(description));
		String sortorderStr = new String(decoder.decode(sortorder));
		Integer offset = Integer.parseInt(offsetStr);
		Integer limit = Integer.parseInt(limitStr);
		List<Item> items = service.paginatesearch(offset, limit, itemnumberStr, descriptionStr, sortorderStr);
		return Response.ok(items).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("paginatecount")
	public Response paginatecount(@QueryParam("itemnumber") String itemnumber, @QueryParam("description") String description) {

		Decoder decoder = Base64.getDecoder();
		String itemnumberStr = new String(decoder.decode(itemnumber));
		String descriptionStr = new String(decoder.decode(description));
		Long items = service.paginatecount(itemnumberStr, descriptionStr);
		return Response.ok(items).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("promptpaginatesearch")
	public Response promptPaginatesearch(@QueryParam("offset") String offs, @QueryParam("limit") String lim, @QueryParam("itemnumber") String itemnumber, @QueryParam("description") String description) {

		Decoder decoder = Base64.getDecoder();
		String offsetStr = new String(decoder.decode(offs));
		String limitStr = new String(decoder.decode(lim));
		String itemnumberStr = new String(decoder.decode(itemnumber));
		String descriptionStr = new String(decoder.decode(description));
		Integer offset = Integer.parseInt(offsetStr);
		Integer limit = Integer.parseInt(limitStr);
		List<SearchData> items = service.PROMPTpaginatesearch(offset, limit, itemnumberStr, descriptionStr);
		return Response.ok(items).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "promptpaginatecount")
	public Response promptPaginateCount(@QueryParam("itemnumber") String cn, @QueryParam("description") String n) {

		Decoder decoder = Base64.getDecoder();
		String itemnumber = new String(decoder.decode(cn));
		String description = new String(decoder.decode(n));
		Long recs = service.PROMPTpaginatecount(itemnumber, description);
		return Response.ok(recs).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("itemnumbers")
	public Response getAllItemNumbers() {
		List<String> items = service.getAllItemIds();
		return Response.ok(items).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("orderinginfo")
	public Response getAllForOrdering() {
		List<ItemView1> items = service.getAllForOrdering();
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