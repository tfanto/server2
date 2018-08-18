package com.fnt.sys;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.fnt.rest.AppUserResource;
import com.fnt.rest.CustomerOrderResource;
import com.fnt.rest.CustomerResource;
import com.fnt.rest.ItemResource;
import com.fnt.rest.QueueResource;

@ApplicationPath("rest")
public class JAXRSConfiguration extends Application {
	private final Set<Object> singletons = new HashSet<>();
	private final Set<Class<?>> set = new HashSet<>();

	public JAXRSConfiguration() {
		// mandatory
		set.add(AppServletContextListener.class);
		set.add(AppRequestFilter.class);
		set.add(AppResponseFilter.class);
		set.add(AppExceptionMapper.class);
		set.add(QueueResource.class);
		set.add(AppUserResource.class);

		// the app
		set.add(ItemResource.class);
		set.add(CustomerResource.class);
		set.add(CustomerOrderResource.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
