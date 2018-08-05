package com.fnt.service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.dto.CustomerOrder;
import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.dto.CustomerOrderLineListView;
import com.fnt.entity.Customer;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.entity.CustomerOrderLinePK;
import com.fnt.entity.Item;
import com.fnt.message.AppJMSMessageProducer;
import com.fnt.sys.AppException;
import com.fnt.sys.SqlFilter;

@Stateless
public class CustomerOrderService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ItemService itemService;

	@Inject
	private CustomerService customerService;

	@Inject
	private AppJMSMessageProducer queueProducer;

	private ObjectMapper MAPPER = null;

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
			head.setDate(LocalDate.now());
		}
		// todo get this from logged on user
		head.setChangedby("BATCH");

		String internalordernumber = UUID.randomUUID().toString();

		head.setInternalordernumber(internalordernumber);
		createHeader(head);

		List<CustomerOrderLine> lines = customerOrder.getLines();
		if (lines != null) {
			long lineNumber = 0;

			for (CustomerOrderLine line : lines) {
				lineNumber = lineNumber + 1;

				CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
				primaryKey.setLineNumber(lineNumber);
				primaryKey.setInternalordernumber(internalordernumber);
				line.setPrimarykey(primaryKey);

				Long itemId = line.getItemid();
				if (itemId == null) {
					throw new IllegalArgumentException("Order line Item Id is null line + " + lineNumber);
				}
				// get item from db
				Item fetchedItem = itemService.get(itemId);
				if (fetchedItem != null) {
					// set price from db
					line.setPriceperitem(fetchedItem.getPrice());
				} else {
					// if not in db - what to do
					handleMissingItems(customerOrder.getHead().getCustomerid(), internalordernumber, itemId);
				}

				// null check
				if (line.getNumberofitems() == null || line.getNumberofitems() == 0) {
					throw new AppException(412, "Order line Number of Items  + " + lineNumber);
				}

				if (line.getDate() == null) {
					line.setDate(head.getDate());
				}
				createLine(internalordernumber, lineNumber, fetchedItem.getItemnumber(), line.getNumberofitems(), fetchedItem.getPrice(), head.getChangedby());
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

	// used from batch
	public CustomerOrderHead createHeader(CustomerOrderHead customerOrderHead) {
		em.persist(customerOrderHead);
		return customerOrderHead;
	}

	public CustomerOrderHead createHeader(String customernumber, String date, String changedby) {

		// datum
		LocalDate dateTime = null;
		if (date.length() > 0) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dateTime = LocalDate.parse(date, formatter);
		}

		// kundid från kundnummer
		if (customernumber == null) {
			throw new AppException(412, "Customernumber is null. Cannot place an customerorder");
		}
		Customer customer = customerService.getByCustomernumber(customernumber);
		if (customer == null) {
			throw new AppException(412, "Customer does not exist." + customernumber);
		}

		// interna ordernumret
		String internalordernumber = UUID.randomUUID().toString();

		// status prliminär = 1
		// chgby
		if ((changedby == null) || (changedby.trim().length() < 1)) {
			changedby = "SYS";
		}

		CustomerOrderHead customerOrderHead = new CustomerOrderHead();
		customerOrderHead.setChangedby(changedby);
		customerOrderHead.setCustomerid(customer.getId());
		customerOrderHead.setDate(dateTime);
		customerOrderHead.setInternalordernumber(internalordernumber);
		customerOrderHead.setStatus(1);
		em.persist(customerOrderHead);
		return customerOrderHead;
	}

	public void deleteHeader(String internalOrderNumber) {

		// Query query = em.createQuery("DELETE FROM CustomerOrderLine ol where
		// ol.PrimaryKey.OrderNumber=:orderNumber");
		// query.setParameter("orderNumber", internalOrderNumber);
		// query.executeUpdate();

		// em.remove(customerOrderHead);
	}

	public Boolean exists(String internalOrderNumber) {
		CustomerOrderHead ret = em.find(CustomerOrderHead.class, internalOrderNumber);
		return ret != null;
	}

	public List<CustomerOrderHead> getAll() {
		TypedQuery<CustomerOrderHead> query = em.createNamedQuery(CustomerOrderHead.CUSTOMER_ORDERHEAD_GET_ALL, CustomerOrderHead.class);
		return query.getResultList();
	}

	public void deleteAll() {

		Query query1 = em.createQuery("DELETE FROM CustomerOrderLine");
		query1.executeUpdate();

		Query query2 = em.createQuery("DELETE FROM CustomerOrderHead");
		query2.executeUpdate();
	}

	public CustomerOrderLine createLine(String internalordernumber, Long linenumber, String itemnumber, Integer numberofitems, Double priceperitem, String changedby) {

		// get item from db must have itemId
		Item fetchedItem = itemService.getByItemNumber(itemnumber);
		if (fetchedItem == null) {
			throw new AppException(412, "Item does not exist." + itemnumber);
		}

		// todo dont forget to update lagersaldo

		CustomerOrderLine customerOrderLine = new CustomerOrderLine();
		CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
		primaryKey.setInternalordernumber(internalordernumber);
		primaryKey.setLineNumber(linenumber);
		customerOrderLine.setPrimarykey(primaryKey);

		customerOrderLine.setDate(LocalDate.now());
		customerOrderLine.setItemid(fetchedItem.getId());
		customerOrderLine.setNumberofitems(numberofitems);
		customerOrderLine.setPriceperitem(priceperitem);
		customerOrderLine.setChangedby(changedby);
		em.persist(customerOrderLine);
		return customerOrderLine;
	}

	// from interactive
	public CustomerOrderLine createLine(String internalordernumber, String itemnumber, Integer numberofitems, Double priceperitem, String changedby) {
		Long lineNumber = System.nanoTime(); // linenumber in timeorder and with internalordernumber is unique
		return createLine(internalordernumber, lineNumber, itemnumber, numberofitems, priceperitem, changedby);
	}

	public void deleteOrderLine(Integer orderNumber, Integer orderLineNumber) {

		// todo dont forget to update lagersaldo

	}

	private SqlFilter createFilterpartForPagination(String sqlFirstPart, String customernumber, String name, String orderdateStr, String orderstatus, String changedby) {

		LocalDate dateTime = null;
		if (orderdateStr.length() > 0) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dateTime = LocalDate.parse(orderdateStr, formatter);
		}

		SqlFilter filter = new SqlFilter();
		String where_and = " where ";
		String sql = sqlFirstPart;

		if (customernumber.length() > 0) {
			sql += where_and;
			if (customernumber.indexOf("%") < 0) {
				sql += " customernumber = :customernumber";
			} else {
				sql += " customernumber like :customernumber";
			}
			filter.params.put("customernumber", customernumber);
			where_and = " and ";
		}

		if (name.length() > 0) {
			sql += where_and;
			if (name.indexOf("%") < 0) {
				sql += " name = :name";
			} else {
				sql += " name like :name";
			}
			filter.params.put("name", name);
			where_and = " and ";
		}

		if (dateTime != null) {
			sql += where_and;
			sql += " customer_order_head.date  >= :orderdate";
			filter.params.put("orderdate", dateTime);
			where_and = " and ";
		}

		if (orderstatus.length() > 0) {
			sql += where_and;
			if (orderstatus.indexOf("%") < 0) {
				sql += " cast(customer_order_head.status as character varying(3)) = :orderstatus";
			} else {
				sql += " cast(customer_order_head.status as character varying(3)) like :orderstatus";
			}
			filter.params.put("orderstatus", orderstatus);
			where_and = " and ";
		}

		if (changedby.length() > 0) {
			sql += where_and;
			if (changedby.indexOf("%") < 0) {
				sql += " changedby = :changedby";
			} else {
				sql += " changedby like :changedby";
			}
			filter.params.put("changedby", changedby);
			where_and = " and ";
		}
		filter.sql = sql;
		return filter;
	}

	public List<CustomerOrderHeadListView> paginatesearch(Integer offset, Integer limit, String customernumber, String name, String orderdateStr, String orderstatus, String changedby, String sortorder) {

		String sort = "";
		if (sortorder.length() > 0) {
			sortorder = sortorder.toLowerCase();
			sort = " order by " + sortorder;
		}

		// @formatter:off		
		String sqlFirstpart = 				
		"select customer_order_head.ordernumber as id, "  +
	    "   customer.customernumber as customernumber, " +
	    "   customer.name as name,  " +
	    "   customer_order_head.date as orderdate , "  +
	    "   customer_order_head.changedby as changedby, " + 
	    "   customer_order_head.status as orderstatus " +
	    "  from customer_order_head  " + 
        "  join customer  " +
        "     on customer.id = customer_order_head.customerid ";  		
		// @formatter:on

		SqlFilter sqlFilter = createFilterpartForPagination(sqlFirstpart, customernumber, name, orderdateStr, orderstatus, changedby);
		String sql = sqlFilter.sql;
		sql += sort;

		Query query = em.createNativeQuery(sql);
		for (Map.Entry<String, Object> entry : sqlFilter.params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		List<CustomerOrderHeadListView> resultSet = new ArrayList<>();
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		@SuppressWarnings("unchecked")
		List<Object[]> rs = query.getResultList();
		for (Object record[] : rs) {
			CustomerOrderHeadListView line = new CustomerOrderHeadListView(record);
			resultSet.add(line);
		}
		return resultSet;
	}

	public Long paginatecount(String customernumber, String name, String orderdateStr, String orderstatus, String changedby) {

		// @formatter:off		
		String sqlFirstpart = 				
		"select count(customer.id) "  +
	    "  from customer_order_head  " + 
        "  join customer  " +
        "     on customer.id = customer_order_head.customerid ";  		
		// @formatter:on
		SqlFilter sqlFilter = createFilterpartForPagination(sqlFirstpart, customernumber, name, orderdateStr, orderstatus, changedby);
		String sql = sqlFilter.sql;

		Query query = em.createNativeQuery(sql);
		for (Map.Entry<String, Object> entry : sqlFilter.params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Object rs = query.getSingleResult();
		BigInteger records = (BigInteger) rs;
		return records.longValue();
	}

	public CustomerOrderHead getById(Long ordernumber) {
		return em.find(CustomerOrderHead.class, ordernumber);
	}

	public CustomerOrderHead updateHeader(Long ordernumber, String customernumber, String date, String changedby) {

		LocalDate dateTime = null;
		if (date.length() > 0) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dateTime = LocalDate.parse(date, formatter);
		}

		Customer customer = customerService.getByCustomernumber(customernumber);
		if (customer == null) {
			throw new AppException(412, "Customer does not exist." + customernumber);
		}

		CustomerOrderHead header = em.find(CustomerOrderHead.class, ordernumber);
		header.setChangedby(changedby);
		header.setDate(dateTime);
		header.setCustomerid(customer.getId());
		return em.merge(header);

	}

	public List<CustomerOrderLineListView> getLinesForOrder(String internalordernumber) {
		// @formatter:off
		
		String sql = 
				
		"select customer_order_line.internalordernumber as internalordernumber, " + 
		"   customer_order_line.linenumber as linenumber, " + 
		"   customer_order_line.date as date, " +
		"   item.itemnumber as itemnumber, " +
		"   item.description as description, " +
		"   customer_order_line.number_of_items as numberofitems, " + 
		"   customer_order_line.price_per_item as priceperitem " +
		"      from customer_order_line  " +
		"      join item  " +
		"         on customer_order_line.item_id = item.id  " +
		"             where customer_order_line.internalordernumber = :internalordernumber";
		  		
		// @formatter:on

		Query query = em.createNativeQuery(sql);
		query.setParameter("internalordernumber", internalordernumber);

		List<CustomerOrderLineListView> resultSet = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Object[]> rs = query.getResultList();
		for (Object record[] : rs) {
			CustomerOrderLineListView line = new CustomerOrderLineListView(record);
			resultSet.add(line);
		}

		return resultSet;
	}

	/*
	 * 1 get all lines 2 for every line increase saldo in item with lines noOfItems
	 * 3 remove the line
	 * 
	 * 4 when ready delete the header
	 * 
	 */
	public void delete(String internalordernumber) {

		List<CustomerOrderLineListView> lines = getLinesForOrder(internalordernumber);
		for (CustomerOrderLineListView line : lines) {
			Long linenumber = line.getLinennumber();
			CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
			primaryKey.setInternalordernumber(internalordernumber);
			primaryKey.setLineNumber(linenumber);
			CustomerOrderLine theLine = new CustomerOrderLine();
			theLine.setPrimarykey(primaryKey);
			em.remove(em.contains(theLine) ? theLine : em.merge(theLine));
		}
		Query qry = em.createNamedQuery(CustomerOrderHead.CUSTOMER_ORDERHEAD_DELETE_BY_INTERNAL_ORDERNUMBER);
		qry.setParameter("internalordernumber", internalordernumber);
		qry.executeUpdate();
	}

	public void deleteCustomerOrderLine(String internalordernumber, Long linenumber, String itemnumber) {

		CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
		primaryKey.setInternalordernumber(internalordernumber);
		primaryKey.setLineNumber(linenumber);
		CustomerOrderLine theLine = new CustomerOrderLine();
		theLine.setPrimarykey(primaryKey);

		CustomerOrderLine fetched = em.find(CustomerOrderLine.class, primaryKey);
		if (fetched != null) {
			em.remove(fetched);
		}

	}

}
