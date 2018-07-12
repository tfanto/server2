package com.fnt.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fnt.dao.CustomerDao;
import com.fnt.dao.CustomerOrderDao;
import com.fnt.dao.ItemDao;
import com.fnt.dto.CustomerOrder;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.entity.CustomerOrderLinePK;
import com.fnt.entity.Item;
import com.fnt.message.AppJMSMessageProducer;

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

	public void create(CustomerOrder customerOrder) {

		if (customerOrder == null) {
			throw new IllegalArgumentException("Customer Order is null");
		}
		CustomerOrderHead head = customerOrder.getHead();
		if (head == null) {
			throw new IllegalArgumentException("Customer Order Header is null");
		}
		if (head.getCustomerId() == null) {
			throw new IllegalArgumentException("Customer Order Header Primary Key Customer Id is null");
		}
		if (!customerDao.exists(head.getCustomerId())) {
			throw new IllegalArgumentException("Customer does not exist " + customerOrder.getHead().getCustomerId());
		}
		if (head.getDate() == null) {
			head.setDate(LocalDateTime.now());
		}

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
					handleMissingItems(customerOrder.getHead().getCustomerId(), internalOrderNumber, itemId);
				}

				// null check
				if (line.getNumberOfItems() == null) {
					throw new IllegalArgumentException("Order line Number of Items  + " + lineNumber);
				}

				// n - check
				if (line.getNumberOfItems() == 0) {
					throw new IllegalArgumentException("Order line Number of items + " + lineNumber);
				}

				if (line.getDate() == null) {
					line.setDate(head.getDate());
				}

				customerOrderDao.addOrderLine(internalOrderNumber, lineNumber, line);
			}
		}
	}

	private void handleMissingItems(Long customerId, String internalOrderNumber, Long itemId) {

		// restorder or just skip that line ?
		// notify the user in each case

	}

	public void post(String customerOrderJson) {
		queueProducer.post(customerOrderJson);
	}
}
