package com.fnt.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fnt.entity.Customer;

@Stateless
public class CustomerDao {

	@PersistenceContext
	private EntityManager em;

	public Customer create(Customer customer) {
		em.persist(customer);
		return customer;
	}

	public Customer update(Customer customer) {
		return em.merge(customer);
	}

	public void delete(Customer customer) {
		em.remove(customer);
	}

	public Customer get(Long id) {
		Customer ret = em.find(Customer.class, id);
		return ret;
	}

	public Customer get(String customernumber) {
		TypedQuery<Customer> query = em.createNamedQuery(Customer.CUSTOMER_GET_BY_CUSTOMERNUMBER, Customer.class);
		query.setParameter("customernumber", customernumber);
		Customer customer = query.getSingleResult();
		return customer;
	}

	public List<Customer> search(String customernumber, String name, String sortorder) {

		String sort = "";
		if (sortorder.length() > 0 ) {
			sortorder = sortorder.toLowerCase();
			sortorder = "u." + sortorder;
			sortorder = sortorder.replaceAll(",", ",u.");
			sort = " order by " + sortorder;			
		}
		
		String where_and = " where ";
		String sql = "select u  from Customer u ";
		Map<String, Object> params = new HashMap<>();

		if (customernumber.length() > 0) {
			sql += where_and;

			if (customernumber.indexOf("%") < 0) {
				sql += " u.customernumber = :customernumber";
			} else {
				sql += " u.customernumber like :customernumber";
			}
			params.put("customernumber", customernumber);
			where_and = " and ";
		}

		if (name.length() > 0) {
			sql += where_and;

			if (name.indexOf("%") < 0) {
				sql += " u.name = :name";
			} else {
				sql += " u.name like :name";
			}
			params.put("name", name);
			where_and = " and ";
		}

		sql += sort;

		TypedQuery<Customer> query = em.createQuery(sql, Customer.class);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	public List<Long> getAllCustomerIds() {
		Query query = em.createQuery("SELECT c.id FROM Customer c");
		@SuppressWarnings("unchecked")
		List<Long> ids = query.getResultList();
		return ids;
	}

	public int deleteAll() {
		Query query = em.createQuery("DELETE FROM Customer");
		return query.executeUpdate();
	}


}
