package com.fnt.sys;

import java.security.Principal;
import java.util.Map;

public class AppUserContext implements Principal {

	private final Map<String, Object> payload;

	AppUserContext(Map<String, Object> payload) {
		this.payload = payload;
	}

	@Override
	public String getName() {
		String name = String.valueOf(payload.get("name"));
		return name;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}

}
