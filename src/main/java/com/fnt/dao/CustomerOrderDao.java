package com.fnt.dao;

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

	public void addOrderLine(String internalOrderNumber, int lineNumber, CustomerOrderLine customerOrderLine) {

		CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
		primaryKey.setInternalordernumber(internalOrderNumber);
		primaryKey.setLineNumber(lineNumber);
		customerOrderLine.setPrimaryKey(primaryKey);
		em.persist(customerOrderLine);

	}

	public void deleteOrderLine(Integer orderNumber, Integer orderLineNumber) {

	}

	public List<CustomerOrderHeadListView> search(String customernumber, String name, String orderdate, String orderstatus, String changedby, String sortorder) {

		// @formatter:off
		
		String sql = 
		
		"select customer_order_head.ordernumber as id, "  +
	    "   customer.customernumber as customernumber, " +
	    "   customer.name as name,  " +
	    "   customer_order_head.date as orderdate , "  +
	    "   customer_order_head.changedby as changedby, " + 
	    "   customer_order_head.status as orderstatus, " +
	    "   cast(customer_order_head.date as character varying(30)) as dateforsearch, " +
	    "   cast(customer_order_head.status as character varying(3)) as statusforsearch" +		
	    "  from customer_order_head  " + 
        "  join customer  " +
        "     on customer.id = customer_order_head.customerid ";
  
		
		// @formatter:on

		sortorder = sortorder.toLowerCase();
		String where_and = " where ";
		Map<String, Object> params = new HashMap<>();
		String sort = "";
		if (sortorder.length() > 0) {
			sortorder = sortorder.toLowerCase();
			//sortorder = "coh." + sortorder;
			//sortorder = sortorder.replaceAll(",", ",coh.");
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
		
		
		
		if (orderdate.length() > 0) {
			sql += where_and;
			if (orderdate.indexOf("%") < 0) {
				sql += " cast(customer_order_head.date as character varying(30)) = :orderdate";
			} else {
				sql += " cast(customer_order_head.date as character varying(30)) like :orderdate";
			}
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
		@SuppressWarnings("unchecked")
		List<Object[]> rs = query.getResultList();
		for (Object record[] : rs) {
			CustomerOrderHeadListView line = new CustomerOrderHeadListView(record);
			resultSet.add(line);
		}

		return resultSet;

	}

}
