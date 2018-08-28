package com.fnt.rest;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

//  https://www.programcreek.com/java-api-examples/?code=readlearncode/Java-EE-8-Sampler/Java-EE-8-Sampler-master/jax-rs-2-1/src/main/java/com/readlearncode/sse/ServerSentEventsResource.java#

@ApplicationScoped
@Path("events")
public class EventsResourceOLD {

	private final Object outputLock = new Object();
	private volatile SseEventSink eventSink;

	@Context
	private Sse sse;

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void openEventStream(@Context final SseEventSink eventSink) {
		synchronized (outputLock) {
			if (this.eventSink != null) {
				throw new IllegalStateException("Server sink already served.");
			}
			this.eventSink = eventSink;
		}
	}

	@POST
	public void sendEvent(final String message) throws IOException {
		if (eventSink == null) {
			throw new IllegalStateException("No client connected.");
		}
		if(eventSink.isClosed()) {
			return;
		}
		
		eventSink.send(sse.newEvent(message));

		// send simple string event
		// OutboundSseEvent stringEvent = sse.newEvent("stringEvent", message + " From
		// server.");
		// eventSink.send(stringEvent);

		// send primitive long event using builder
		// OutboundSseEvent primitiveEvent =
		// sse.newEventBuilder().name("primitiveEvent").data(System.currentTimeMillis()).build();
		// eventSink.send(primitiveEvent);

		// send JSON-B marshalling to send event
		// @formatter:off
		 /*
		OutboundSseEvent jsonbEvent = sse
				.newEventBuilder()
				.name("jsonbEvent")
				.data(new JsonbSseEvent(message))
				.mediaType(MediaType.APPLICATION_JSON_TYPE)
				.build();
		eventSink.send(jsonbEvent);
		*/
		// @formatter:on
	}

	@DELETE
	public void closeEventStream() throws IOException {
		synchronized (outputLock) {
			if (eventSink != null) {
				eventSink.close();
				eventSink = null;
			}
		}
	}

	@JsonbPropertyOrder({ "time", "message" })
	public static class JsonbSseEvent {
		String message;

		LocalDateTime today = LocalDateTime.now();

		public JsonbSseEvent(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public LocalDateTime getToday() {
			return today;
		}

		public void setToday(LocalDateTime today) {
			this.today = today;
		}
	}
}
