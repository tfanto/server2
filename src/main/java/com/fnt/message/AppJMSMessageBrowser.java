package com.fnt.message;

import java.util.Enumeration;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class AppJMSMessageBrowser {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppJMSMessageBrowser.class);

	@Resource(mappedName = AppJMSConstants.APP_CONNECTION_FACTORY)
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = AppJMSConstants.APP_QUEUE)
	private Queue queue;

	public void browseMessages() {

		JMSContext jmsContext = null;
		try {
			@SuppressWarnings("rawtypes")
			Enumeration messageEnumeration;
			TextMessage textMessage;
			jmsContext = connectionFactory.createContext();
			QueueBrowser browser = jmsContext.createBrowser(queue);
			messageEnumeration = browser.getEnumeration();
			if (messageEnumeration != null) {
				while (messageEnumeration.hasMoreElements()) {
					textMessage = (TextMessage) messageEnumeration.nextElement();
					LOGGER.info(textMessage.getText());
				}
			}
		} catch (JMSException e) {
			LOGGER.info(e.toString(), e);
		} finally {
			if (jmsContext != null) {
				jmsContext.close();
			}
		}
	}

}
