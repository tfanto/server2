package com.fnt.rest;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.dto.CustomerOrderLineListView;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.service.CustomerOrderService;
import com.fnt.sys.AppServletContextListener;

@Path("customerorder")

public class CustomerOrderResource {

	@Inject
	private CustomerOrderService service;

	@Context
	private SecurityContext sc;
	


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
	public Response createCustomerOrderHeader(@QueryParam("customernumber") String customernumberStr, @QueryParam("date") String dateStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String customernumber = new String(decoder.decode(customernumberStr));
		String date = new String(decoder.decode(dateStr));
		String userName = sc.getUserPrincipal().getName();
		CustomerOrderHead obj = service.createHeader(customernumber, date, userName);
		
		return Response.ok(obj).type(MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("line")
	public Response createCustomerOrderLine(@QueryParam("internalordernumber") String internalordernumberStr, @QueryParam("itemnumber") String itemnumberStr, @QueryParam("units") String unitsStr,
			@QueryParam("priceperitem") String priceperitemStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String internalordernumber = new String(decoder.decode(internalordernumberStr));
		String itemnumber = new String(decoder.decode(itemnumberStr));
		String unitsDecoded = new String(decoder.decode(unitsStr));
		String priceperitemDecoded = new String(decoder.decode(priceperitemStr));
		Integer units = Integer.parseInt(unitsDecoded);
		Double priceperitem = Double.parseDouble(priceperitemDecoded);
		String userName = sc.getUserPrincipal().getName();
		CustomerOrderLine obj = service.createLine(internalordernumber, itemnumber, units, priceperitem, userName);
		return Response.ok(obj).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "linesfororder/{internalordernumber}")
	public Response getLinesForOrder(@PathParam("internalordernumber") String internalordernumberStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String internalordernumber = new String(decoder.decode(internalordernumberStr));
		List<CustomerOrderLineListView> obj = service.getLinesForOrder(internalordernumber);
		return Response.ok(obj).type(MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path("header")
	public Response updateCustomerOrderHeader(@QueryParam("ordernumber") String ordernumberStr, @QueryParam("customernumber") String customernumberStr, @QueryParam("date") String dateStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String customernumber = new String(decoder.decode(customernumberStr));
		String date = new String(decoder.decode(dateStr));
		Long ordernumber = Long.parseLong(ordernumberStr);
		String userName = sc.getUserPrincipal().getName();
		CustomerOrderHead obj = service.updateHeader(ordernumber, customernumber, date, userName);
		return Response.ok(obj).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "paginatesearch")
	public Response paginatesearch(@QueryParam("offset") String offs, @QueryParam("limit") String lim, @QueryParam("customernumber") String customernumberStr, @QueryParam("name") String nameStr, @QueryParam("date") String dateStr,
			@QueryParam("orderstatus") String orderstatusStr, @QueryParam("changedby") String changedbyStr, @QueryParam("sortorder") String sortorderStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String offsetStr = new String(decoder.decode(offs));
		String limitStr = new String(decoder.decode(lim));
		Integer offset = Integer.parseInt(offsetStr);
		Integer limit = Integer.parseInt(limitStr);
		String customernumber = new String(decoder.decode(customernumberStr));
		String name = new String(decoder.decode(nameStr));
		String date = new String(decoder.decode(dateStr));
		String orderstatus = new String(decoder.decode(orderstatusStr));
		String changedby = new String(decoder.decode(changedbyStr));
		String sortorder = new String(decoder.decode(sortorderStr));
		List<CustomerOrderHeadListView> list = service.paginatesearch(offset, limit, customernumber, name, date, orderstatus, changedby, sortorder);
		return Response.ok(list).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "paginatecount")
	public Response paginatecount(@QueryParam("customernumber") String customernumberStr, @QueryParam("name") String nameStr, @QueryParam("date") String dateStr, @QueryParam("orderstatus") String orderstatusStr,
			@QueryParam("changedby") String changedbyStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String customernumber = new String(decoder.decode(customernumberStr));
		String name = new String(decoder.decode(nameStr));
		String date = new String(decoder.decode(dateStr));
		String orderstatus = new String(decoder.decode(orderstatusStr));
		String changedby = new String(decoder.decode(changedbyStr));
		Long items = service.paginatecount(customernumber, name, date, orderstatus, changedby);
		return Response.ok(items).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "{ordernumber}")
	public Response getById(@PathParam("ordernumber") String ordernumberStr) throws JsonProcessingException {
		Long ordernumber = Long.parseLong(ordernumberStr);
		CustomerOrderHead fetched = service.getById(ordernumber);
		if (fetched == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(Entity.json("Does not exist : " + String.valueOf(ordernumber))).build();
		} else {
			return Response.ok(fetched).type(MediaType.APPLICATION_JSON).build();
		}
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "{internalordernumber}")
	public Response deleteCustomerOrder(@PathParam("internalordernumber") String internalordernumberStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String internalordernumber = new String(decoder.decode(internalordernumberStr));
		service.delete(internalordernumber);
		return Response.ok().build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER" })
	@Path(value = "{internalordernumber}/{linenumber}/{itemnumber}")
	public Response deleteCustomerOrderLine(@PathParam("internalordernumber") String internalordernumberStr, @PathParam("linenumber") String linenumberStr, @PathParam("itemnumber") String itemnumberStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String internalordernumber = new String(decoder.decode(internalordernumberStr));
		String ln = new String(decoder.decode(linenumberStr));
		Long linenumber = Long.parseLong(ln);
		String itemnumber = new String(decoder.decode(itemnumberStr));
		service.deleteCustomerOrderLine(internalordernumber, linenumber, itemnumber);
		return Response.ok().build();
	}
		
}
