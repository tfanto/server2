package com.fnt.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
		TypedQuery<CustomerOrderHead> query = em.createNamedQuery(CustomerOrderHead.CUSTOMER_ORDERHEAD_GET_ALL,
				CustomerOrderHead.class);
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

}
