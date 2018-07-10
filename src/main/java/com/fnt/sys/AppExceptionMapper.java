package com.fnt.sys;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppException> {

	@Override
	public Response toResponse(AppException e) {
		return Response.status(e.getCode().intValue()).entity(e.getMsg()).build();
	}

}
