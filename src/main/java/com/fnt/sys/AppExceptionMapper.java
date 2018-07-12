package com.fnt.sys;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Provider
public class AppExceptionMapper implements ExceptionMapper<RuntimeException> {

	private ObjectMapper getMapper() {
		ObjectMapper MAPPER = new ObjectMapper();
		MAPPER.registerModule(new JavaTimeModule());
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return MAPPER;
	}

	final class MessageFormat {

		private Integer code;
		private String appMsg;
		private String cause = "unknown";

		public MessageFormat() {
		}

		public MessageFormat(Integer code, String appMsg, Throwable cause) {
			this.code = code;
			this.appMsg = appMsg;
			if (cause != null) {
				this.cause = cause.getMessage();
			}
		}

		public Integer getCode() {
			return code;
		}

		public String getAppMsg() {
			return appMsg;
		}

		public String getCause() {
			return cause;
		}
	}

	@Override
	public Response toResponse(RuntimeException r) {

		if (r instanceof AppException) {
			AppException e = (AppException) r;
			MessageFormat fmt = new MessageFormat(e.getCode(), e.getMessage(), e.getCause());
			String json = toJson(fmt);
			return Response.status(e.getCode().intValue()).entity(json).build();
		} else {
			MessageFormat fmt = new MessageFormat(400, r.getMessage(), r.getCause());
			String json = toJson(fmt);
			return Response.status(400).entity(json).build();
		}
	}

	private String toJson(MessageFormat fmt) {

		try {
			String json = getMapper().writeValueAsString(fmt);
			return json;
		} catch (JsonProcessingException e1) {
			String msg = e1.getMessage();
			return "{\"code\":500,\"appMsg\":\"Internalerror\",\"cause\":\"" + msg + "\"}";
		}
	}

}
