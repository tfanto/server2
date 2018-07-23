package com.fnt.rest;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.dto.CustomerOrderHeadListView;
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
	public Response createCustomerOrderHeader(@QueryParam("customernumber") String customernumberStr, @QueryParam("date") String dateStr) throws JsonProcessingException {
		
		Decoder decoder = Base64.getUrlDecoder();
		String customernumber = new String(decoder.decode(customernumberStr));
		String date = new String(decoder.decode(dateStr));
		
		CustomerOrderHead obj = service.createHeader(customernumber, date, "SYS");
		String json = MAPPER.writeValueAsString(obj);
		return Response.ok(json).build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ADMIN", "USER", "GUEST" })
	@Path(value = "search")
	public Response search(@QueryParam("customernumber") String customernumberStr, @QueryParam("name") String nameStr, @QueryParam("date") String dateStr, @QueryParam("orderstatus") String orderstatusStr,
			@QueryParam("changedby") String changedbyStr, @QueryParam("sortorder") String sortorderStr) throws JsonProcessingException {

		Decoder decoder = Base64.getUrlDecoder();
		String customernumber = new String(decoder.decode(customernumberStr));
		String name = new String(decoder.decode(nameStr));
		String date = new String(decoder.decode(dateStr));
		String orderstatus = new String(decoder.decode(orderstatusStr));
		String changedby = new String(decoder.decode(changedbyStr));
		String sortorder = new String(decoder.decode(sortorderStr));

		List<CustomerOrderHeadListView> list = service.search(customernumber, name, date, orderstatus, changedby, sortorder);

		String json = MAPPER.writeValueAsString(list);

		return Response.ok(json).build();
	}

}
