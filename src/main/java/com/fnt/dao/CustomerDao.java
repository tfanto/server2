package com.fnt.dao;

import java.util.List;

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

	public Customer get(String id) {
		Customer ret = em.find(Customer.class, id);
		return ret;
	}

	public Boolean exists(String id) {
		Customer ret = em.find(Customer.class, id);
		return ret != null;
	}

	public List<Customer> getAll() {
		TypedQuery<Customer> query = em.createNamedQuery(Customer.CUSTOMER_GET_ALL, Customer.class);
		return query.getResultList();
	}
	
	public List<String> getAllCustomerIds() {
		Query query = em.createQuery("SELECT c.id FROM Customer c");
		@SuppressWarnings("unchecked")
		List<String> ids = query.getResultList();		
		return ids;
	}

	public int deleteAll() {
		Query query = em.createQuery("DELETE FROM Customer");
		return query.executeUpdate();
	}

}
