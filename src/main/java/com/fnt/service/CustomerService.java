package com.fnt.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fnt.AppException;
import com.fnt.dto.SearchData;
import com.fnt.entity.Customer;
import com.fnt.rest.DomainEvent;
import com.fnt.sys.SqlFilter;

@Stateless
public class CustomerService {

	private static final Integer HTTP_PRECONDITION_FAILED = 412;

	@Inject
	Event<DomainEvent> domainEvents;

	@PersistenceContext
	private EntityManager em;

	public Customer create(Customer customer) {
		if (customer == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		em.persist(customer);
		domainEvents.fire(new DomainEvent("CRT:CUNO:" + String.valueOf(customer.getCustomernumber() + ":" + Instant.now())));
		return customer;
	}

	public Customer update(Customer customer) {

		if (customer == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		if (customer.getId() == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity primary key must NOT be null at update");
		}

		Customer merged = em.merge(customer);
		domainEvents.fire(new DomainEvent("CHG:CUNO:" + String.valueOf(customer.getCustomernumber() + ":" + Instant.now())));
		return em.merge(merged);
	}

	public void delete(Long id) {
		Customer customer = get(id);
		em.remove(customer);
	}

	public void delete(Customer customer) {
		em.remove(customer);
	}

	public Customer get(Long id) {
		if (id == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Id is null");
		}
		Customer ret = em.find(Customer.class, id);
		return ret;
	}

	public Customer getByCustomernumber(String customernumber) {

		if (customernumber == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Customernumber is null");
		}
		TypedQuery<Customer> query = em.createNamedQuery(Customer.CUSTOMER_GET_BY_CUSTOMERNUMBER, Customer.class);
		query.setParameter("customernumber", customernumber);
		try {
			Customer customer = query.getSingleResult();
			return customer;
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	private SqlFilter createFilterpartForPagination(String sqlFirstPart, String customernumber, String name) {

		SqlFilter filter = new SqlFilter();
		String where_and = " where ";
		String sql = sqlFirstPart;

		if (customernumber.length() > 0) {
			sql += where_and;

			if (customernumber.indexOf("%") < 0) {
				sql += " u.customernumber = :customernumber";
			} else {
				sql += " u.customernumber like :customernumber";
			}
			filter.params.put("customernumber", customernumber);
			where_and = " and ";
		}

		if (name.length() > 0) {
			sql += where_and;

			if (name.indexOf("%") < 0) {
				sql += " u.name = :name";
			} else {
				sql += " u.name like :name";
			}
			filter.params.put("name", name);
			where_and = " and ";
		}
		filter.sql = sql;
		return filter;
	}

	public List<Customer> paginatesearch(Integer offset, Integer limit, String customernumber, String name, String sortorder) {

		String sort = "";
		if (sortorder.length() > 0) {
			sortorder = sortorder.toLowerCase();
			sortorder = "u." + sortorder;
			sortorder = sortorder.replaceAll(",", ",u.");
			sort = " order by " + sortorder;
		}
		SqlFilter sqlFilter = createFilterpartForPagination("select u  from Customer u ", customernumber, name);
		String sql = sqlFilter.sql;
		sql += sort;
		TypedQuery<Customer> query = em.createQuery(sql, Customer.class);
		for (Map.Entry<String, Object> entry : sqlFilter.params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	/*
	 * the total number of records in a paginated search must have the same search
	 * criteria as the paginated query (filter part)
	 */
	public Long paginatecount(String customernumber, String name) {

		SqlFilter sqlFilter = createFilterpartForPagination("select count(u.id)  from Customer u ", customernumber, name);
		TypedQuery<Long> query = em.createQuery(sqlFilter.sql, Long.class);
		for (Map.Entry<String, Object> entry : sqlFilter.params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Long rs = query.getSingleResult();
		return rs;
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

	public List<SearchData> prompt(String customernumber, String name) {

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

		sql += " order by u.customernumber, u.name";

		TypedQuery<Customer> query = em.createQuery(sql, Customer.class);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		List<Customer> tmpList = query.getResultList();
		List<SearchData> ret = new ArrayList<>();
		tmpList.forEach(cuno -> {
			ret.add(new SearchData(cuno.getCustomernumber(), cuno.getName()));
		});
		tmpList.clear();
		return ret;
	}

}
