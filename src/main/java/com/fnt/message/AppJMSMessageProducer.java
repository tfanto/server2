package com.fnt.message;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;

@ApplicationScoped
public class AppJMSMessageProducer {

	@Resource(mappedName = AppJMSConstants.APP_CONNECTION_FACTORY)
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = AppJMSConstants.APP_QUEUE)
	private Queue queue;

	public void post(String json) {

		JMSContext jmsContext = null;
		try {
			jmsContext = connectionFactory.createContext();
			JMSProducer jmsProducer = jmsContext.createProducer();
			jmsProducer.send(queue, json);
		} finally {
			if (jmsContext != null) {
				jmsContext.close();
			}
		}
	}

}
