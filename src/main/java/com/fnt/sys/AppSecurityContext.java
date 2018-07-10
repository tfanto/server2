package com.fnt.sys;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class AppSecurityContext implements SecurityContext {

	private final AppUserContext userContext;
	private final UriInfo uriInfo;
	private final List<String> roles;

	AppSecurityContext(UriInfo uriInfo, AppUserContext userContext) {
		this.userContext = userContext;
		this.uriInfo = uriInfo;
		String rolesStr = String.valueOf(userContext.getPayload().get("roles"));
		roles = Arrays.asList(rolesStr.split(","));
	}

	@Override
	public Principal getUserPrincipal() {
		return userContext;
	}

	@Override
	public boolean isUserInRole(String allowedRoles) {

		if (allowedRoles == null) {
			return false;
		}

		List<String> allowedList = Arrays.asList(allowedRoles.split(","));
		if(allowedList.size() < 1) return false;
		if(roles.size() < 1) return false;
		for (String r : roles) {
			for (String allowed : allowedList) {
				if (r.trim().equals(allowed.trim()))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSecure() {
		return uriInfo.getAbsolutePath().toString().startsWith("https");
	}

	@Override
	public String getAuthenticationScheme() {
		return "Token-Based-Auth-Scheme";
	}

}
