package com.fnt.rest;

import java.time.Instant;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Startup
@Singleton
public class EventProducer {

	@Inject
	Event<DomainEvent> domainEvents;

	@Schedule(second = "*/10", minute = "*", hour = "*")
	public void produceEvent() {
		domainEvents.fire(new DomainEvent("Hello, " + Instant.now()));
	}

}
