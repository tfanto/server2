package com.fnt.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.dao.CustomerDao;
import com.fnt.dao.CustomerOrderDao;
import com.fnt.dao.ItemDao;
import com.fnt.dto.CustomerOrder;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.entity.CustomerOrderLinePK;
import com.fnt.entity.Item;
import com.fnt.message.AppJMSMessageProducer;
import com.fnt.sys.AppException;

@Stateless
public class CustomerOrderService {

	@Inject
	private CustomerOrderDao customerOrderDao;

	@Inject
	private ItemDao itemDao;

	@Inject
	private AppJMSMessageProducer queueProducer;

	@Inject
	private CustomerDao customerDao;

	private ObjectMapper MAPPER = null;

	@Inject
	private CustomerOrderService service;

	public CustomerOrderService() {
		MAPPER = new ObjectMapper();
		MAPPER.registerModule(new JavaTimeModule());
	}

	public void createBatch(CustomerOrder customerOrder) {

		if (customerOrder == null) {
			throw new AppException(412, "Customer Order is null");
		}
		CustomerOrderHead head = customerOrder.getHead();
		if (head == null) {
			throw new AppException(412, "Customer Order Header is null");
		}
		if (head.getCustomerid() == null) {
			throw new AppException(412, "Customer Order Header Primary Key Customer Id is null");
		}
		if (head.getDate() == null) {
			head.setDate(LocalDateTime.now());
		}
		// todo get this from logged on user
		head.setChangedby("SYS");

		String internalOrderNumber = UUID.randomUUID().toString();

		head.setInternalordernumber(internalOrderNumber);
		customerOrderDao.createHeader(head);

		List<CustomerOrderLine> lines = customerOrder.getLines();
		if (lines != null) {
			int lineNumber = 0;

			for (CustomerOrderLine line : lines) {
				lineNumber = lineNumber + 1;

				CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
				primaryKey.setLineNumber(lineNumber);
				primaryKey.setInternalordernumber(internalOrderNumber);
				line.setPrimaryKey(primaryKey);

				Long itemId = line.getItemId();
				if (itemId == null) {
					throw new IllegalArgumentException("Order line Item Id is null line + " + lineNumber);
				}
				// get item from db
				Item fetchedItem = itemDao.get(itemId);
				if (fetchedItem != null) {
					// set price from db
					line.setPricePerItem(fetchedItem.getPrice());
				} else {
					// if not in db - what to do
					handleMissingItems(customerOrder.getHead().getCustomerid(), internalOrderNumber, itemId);
				}

				// null check
				if (line.getNumberOfItems() == null) {
					throw new AppException(412, "Order line Number of Items  + " + lineNumber);
				}

				// n - check
				if (line.getNumberOfItems() == 0) {
					throw new AppException(412, "Order line Number of items + " + lineNumber);
				}

				if (line.getDate() == null) {
					line.setDate(head.getDate());
				}

				customerOrderDao.addOrderLine(internalOrderNumber, lineNumber, line);
			}
		}
	}

	public CustomerOrderHead createHeader(String headJson) {

		if (headJson == null) {
			throw new AppException(412, "Customer Orderheader is null");
		}
		CustomerOrderHead head;
		try {
			head = MAPPER.readValue(headJson, CustomerOrderHead.class);
		} catch (IOException e) {
			throw new AppException(412, "Customer Orderheader invalid");
		}

		if (head.getCustomerid() == null) {
			throw new AppException(412, "Customer Order Header Primary Key Customer Id is null");
		}
		if (head.getDate() == null) {
			head.setDate(LocalDateTime.now());
		}
		
		// todo get this from logged on user
		head.setChangedby("SYS");
		String internalOrderNumber = UUID.randomUUID().toString();
		head.setInternalordernumber(internalOrderNumber);
		return customerOrderDao.createHeader(head);

	}

	private void handleMissingItems(Long customerId, String internalOrderNumber, Long itemId) {

		// restorder or just skip that line ?
		// notify the user in each case

	}

	public void post(String customerOrderJson) {
		queueProducer.post(customerOrderJson);
	}
}
