package com.fnt.message;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.dto.CustomerOrder;
import com.fnt.service.CustomerOrderService;

@MessageDriven(mappedName = AppJMSConstants.APP_MESSAGE_CONSUMER, activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = AppJMSConstants.APP_QUEUE),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = AppJMSConstants.APP_DESTINATION_TYPE), })
public class AppJMSMessageConsumer implements MessageListener {

	private ObjectMapper MAPPER = null;

	@Inject
	CustomerOrderService customerOrderService;

	public AppJMSMessageConsumer() {
		MAPPER = new ObjectMapper();
		MAPPER.registerModule(new JavaTimeModule());
	}

	@Override
	public void onMessage(Message message) {

		try {
			final String json = message.getBody(String.class);
			CustomerOrder customerOrder = MAPPER.readValue(json, CustomerOrder.class);
			customerOrderService.create(customerOrder);
		} catch (JMSException | IOException e) {
			System.out.println(e.toString());
		}
	}

}
