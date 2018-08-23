package com.fnt.sys;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Priority;
import javax.crypto.SecretKey;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnt.AppException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;

@PreMatching
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AppRequestFilter implements ContainerRequestFilter {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String TRANSACTION_ID = "TRANSID";

	@Context
	UriInfo uriInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String jweString = requestContext.getHeaderString("Authorization");
		if (jweString == null) {
			throw new AppException(403, "Forbidden");
			// requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
		Map<String, Object> payload = null;
		try {
			payload = decrypt(jweString);
		} catch (Throwable t) {
			throw new AppException(403, "Forbidden");
			// requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
		if (payload == null) {
			throw new AppException(403, "Forbidden");
			// requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
		if (!payload.containsKey("user")) {
			throw new AppException(403, "Forbidden");
			// requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
		if (!payload.containsKey("roles")) {
			throw new AppException(403, "Forbidden");
			// requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}

		// transaction tracking every request must be trackable
		MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());

		// AppUserContext user = new AppUserContext(payload);
		// AppSecurityContext securityContext = new AppSecurityContext(uriInfo, user);
		// requestContext.setSecurityContext(securityContext);

		String user = String.valueOf(payload.get("user"));
		String rolesStr = String.valueOf(payload.get("roles"));

		requestContext.setSecurityContext(new SecurityContext() {
			@Override
			public Principal getUserPrincipal() {
				return new Principal() {
					@Override
					public String getName() {
						return user;
					}
				};
			}

			@Override
			public boolean isUserInRole(String role) {
				List<String> roles = Arrays.asList(rolesStr.split(","));
				return roles.contains(role);
			}

			@Override
			public boolean isSecure() {
				return uriInfo.getAbsolutePath().toString().startsWith("https");
			}

			@Override
			public String getAuthenticationScheme() {
				return "Token-Based-Auth-Scheme";
			}

		});
	}

	/*
	 * @Override public void filter(ContainerRequestContext requestContext) throws
	 * IOException {
	 * 
	 * // transaction tracking every request must be trackable
	 * MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
	 * 
	 * Map<String, Object> payload = new HashMap<>(); payload.put("name",
	 * "testuser"); payload.put("roles", "ADMIN,USER,GUEST");
	 * 
	 * AppUserContext user = new AppUserContext(payload); AppSecurityContext
	 * securityContext = new AppSecurityContext(uriInfo, user);
	 * requestContext.setSecurityContext(securityContext); }
	 */

	@SuppressWarnings("unused")
	private Map<String, Object> decrypt(String jweString) throws ParseException, JOSEException, JsonParseException, JsonMappingException, IOException {

		JWEObject jweObject = JWEObject.parse(jweString);

		DirectDecrypter directDecrypter = new DirectDecrypter(getKey());

		jweObject.decrypt(directDecrypter);

		// Get the plain text
		Payload payload = jweObject.getPayload();

		Map<String, Object> payloadMap = MAPPER.readValue(payload.toString(), new TypeReference<Map<String, Object>>() {
		});

		return payloadMap;

	}

	private SecretKey getKey() {
		return AppServletContextListener.getEncryptionKey();
	}

}
