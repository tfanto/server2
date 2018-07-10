package com.fnt.sys;

import javax.ejb.Stateless;
import javax.ws.rs.core.SecurityContext;

@Stateless
public class AppCommonLib {

	public AppCommonLib() {

	}

	public void isAllowed(SecurityContext ctx, String role) {
		if (ctx == null) {
			throw new IllegalAccessError("Not allowed");
		}
		if (role == null) {
			throw new IllegalAccessError("Not allowed");
		}
		if (!ctx.isUserInRole(role)) {
			throw new IllegalAccessError("Not allowed");
		}
	}

}
