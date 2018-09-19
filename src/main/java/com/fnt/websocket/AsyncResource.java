package com.fnt.websocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
@Singleton
public class AsyncResource {


	@OnOpen
	public void onopen(Session s) throws IOException {
		s.getBasicRemote().sendText(String.valueOf(hashCode()));
	}

	@OnMessage
	public synchronized void processMessage(Session session, String message) {

		try {
			for (Session sess : session.getOpenSessions()) {
				if (sess.isOpen()) {
					sess.getBasicRemote().sendText(message);
				}
			}
		} catch (IOException ioe) {
			LOG.log(Level.SEVERE, ioe.getMessage());
		}
	}

	private static final Logger LOG = Logger.getLogger(AsyncResource.class.getName());

	@PreDestroy
	public void onDestroy() {
		LOG.info("Singleton bean " + hashCode() + " will be destroyed");
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		LOG.info("Closed " + session.getId() + " due to " + closeReason.getCloseCode());
	}


}