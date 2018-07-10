package com.fnt.sys;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Provider
public class AppResponseFilter implements ContainerResponseFilter {

	private static boolean useLog = false;

	private static final Logger LOGGER = LoggerFactory.getLogger("rest.api");

	private static final String TRANSACTION_ID = "TRANSID";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		String transId = MDC.get(TRANSACTION_ID) == null ? "" : MDC.get(TRANSACTION_ID);

		String path = requestContext.getUriInfo().getPath();
		String method = requestContext.getMethod();
		Integer status = responseContext.getStatus();
		String msg = responseContext.getStatusInfo().toString();

		if (useLog) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("METHOD  : " + method);
				LOGGER.info("PATH    : " + path);
				LOGGER.info("STATUS  : " + String.valueOf(status));
				LOGGER.info("MSG     : " + msg);
				LOGGER.info(TRANSACTION_ID + " : " + transId);
			}
		}
	}
}
