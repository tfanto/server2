package com.fnt.dao;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.dto.CustomerOrderLineListView;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.entity.CustomerOrderLinePK;

@Stateless
public class CustomerOrderDao {

	@PersistenceContext
	private EntityManager em;

	public CustomerOrderHead createHeader(CustomerOrderHead customerOrderHead) {
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

	public CustomerOrderLine addOrderLine(String internalOrderNumber, long lineNumber, CustomerOrderLine customerOrderLine) {

		CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
		primaryKey.setInternalordernumber(internalOrderNumber);
		primaryKey.setLineNumber(lineNumber);
		customerOrderLine.setPrimarykey(primaryKey);
		em.persist(customerOrderLine);
		return customerOrderLine;
	}

	public void deleteOrderLine(Integer orderNumber, Integer orderLineNumber) {

	}

	public List<CustomerOrderHeadListView> paginatesearch(Integer offset, Integer limit, String customernumber, String name, LocalDate orderdate, String orderstatus, String changedby, String sortorder) {

		// @formatter:off
		
		String sql = 
				
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

		String where_and = " where ";
		Map<String, Object> params = new HashMap<>();
		String sort = "";
		if (sortorder.length() > 0) {
			sortorder = sortorder.toLowerCase();
			sort = " order by " + sortorder;
		}

		if (customernumber.length() > 0) {
			sql += where_and;
			if (customernumber.indexOf("%") < 0) {
				sql += " customernumber = :customernumber";
			} else {
				sql += " customernumber like :customernumber";
			}
			params.put("customernumber", customernumber);
			where_and = " and ";
		}

		if (name.length() > 0) {
			sql += where_and;
			if (name.indexOf("%") < 0) {
				sql += " name = :name";
			} else {
				sql += " name like :name";
			}
			params.put("name", name);
			where_and = " and ";
		}

		if (orderdate != null) {
			sql += where_and;

			sql += " customer_order_head.date  >= :orderdate";

			params.put("orderdate", orderdate);
			where_and = " and ";
		}

		if (orderstatus.length() > 0) {
			sql += where_and;
			if (orderstatus.indexOf("%") < 0) {
				sql += " cast(customer_order_head.status as character varying(3)) = :orderstatus";
			} else {
				sql += " cast(customer_order_head.status as character varying(3)) like :orderstatus";
			}
			params.put("orderstatus", orderstatus);
			where_and = " and ";
		}

		if (changedby.length() > 0) {
			sql += where_and;
			if (changedby.indexOf("%") < 0) {
				sql += " changedby = :changedby";
			} else {
				sql += " changedby like :changedby";
			}
			params.put("changedby", changedby);
			where_and = " and ";
		}

		sql += sort;

		Query query = em.createNativeQuery(sql);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
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

	public Long paginatecount(String customernumber, String name, LocalDate orderdate, String orderstatus, String changedby) {
		// @formatter:off
		
		String sql = 
				
		"select count(customer.id) "  +
	    "  from customer_order_head  " + 
        "  join customer  " +
        "     on customer.id = customer_order_head.customerid ";
  		
		// @formatter:on

		String where_and = " where ";
		Map<String, Object> params = new HashMap<>();

		if (customernumber.length() > 0) {
			sql += where_and;
			if (customernumber.indexOf("%") < 0) {
				sql += " customer.customernumber = :customernumber";
			} else {
				sql += " customer.customernumber like :customernumber";
			}
			params.put("customernumber", customernumber);
			where_and = " and ";
		}

		if (name.length() > 0) {
			sql += where_and;
			if (name.indexOf("%") < 0) {
				sql += " customer.name = :name";
			} else {
				sql += " customer.name like :name";
			}
			params.put("name", name);
			where_and = " and ";
		}

		if (orderdate != null) {
			sql += where_and;
			sql += " customer_order_head.date  >= :orderdate";
			params.put("orderdate", orderdate);
			where_and = " and ";
		}

		if (orderstatus.length() > 0) {
			sql += where_and;
			if (orderstatus.indexOf("%") < 0) {
				sql += " cast(customer_order_head.status as character varying(3)) = :orderstatus";
			} else {
				sql += " cast(customer_order_head.status as character varying(3)) like :orderstatus";
			}
			params.put("orderstatus", orderstatus);
			where_and = " and ";
		}

		if (changedby.length() > 0) {
			sql += where_and;
			if (changedby.indexOf("%") < 0) {
				sql += " customer_order_head.changedby = :changedby";
			} else {
				sql += " customer_order_head.changedby like :changedby";
			}
			params.put("changedby", changedby);
			where_and = " and ";
		}

		Query query = em.createNativeQuery(sql);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		// ugly but ok its a biginteger
		Object rs = query.getSingleResult();
		BigInteger records = (BigInteger) rs;
		return records.longValue();
	}

	public CustomerOrderHead getById(Long ordernumber) {
		return em.find(CustomerOrderHead.class, ordernumber);
	}

	public CustomerOrderHead updateHeader(Long ordernumber, Long customerid, LocalDate date, String changedby) {

		CustomerOrderHead header = em.find(CustomerOrderHead.class, ordernumber);
		header.setChangedby(changedby);
		header.setDate(date);
		header.setCustomerid(customerid);
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

}
