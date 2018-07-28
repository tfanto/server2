package com.fnt.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fnt.dao.CustomerDao;
import com.fnt.dto.SearchData;
import com.fnt.entity.Customer;
import com.fnt.sys.AppException;

@Stateless
public class CustomerService {

	private static final Integer HTTP_PRECONDITION_FAILED = 412;
	private static final Integer HTTP_NOT_FOUND = 404;

	@Inject
	private CustomerDao dao;

	public Customer create(Customer customer) {
		if (customer == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		return dao.create(customer);
	}

	public Customer update(Customer customer) {

		if (customer == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		if (customer.getId() == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity primary key must NOT be null at update");
		}
		return dao.update(customer);

	}

	public void delete(Long id) {

		Customer service = get(id);
		dao.delete(service);
	}

	public Customer get(Long id) {
		if (id == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Id is null");
		}
		Customer fetched = dao.get(id);
		return fetched;
	}

	public Customer getByCustomernumber(String customernumber) {
		if (customernumber == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Customernumber is null");
		}
		Customer fetched = dao.getByCustomernumber(customernumber);
		return fetched;
	}

	public List<Customer> paginatesearch(Integer offset, Integer limit, String customerNumber, String name, String sortorder) {
		return dao.paginatesearch(offset, limit, customerNumber, name, sortorder);
	}
	
	public Long paginatecount(String customernumber, String name) {
		return dao.paginatecount(customernumber, name);
	}

	public List<Long> getAllCustomerIds() {
		return dao.getAllCustomerIds();
	}

	public int deleteAll() {
		return dao.deleteAll();
	}

	public List<SearchData> prompt(String customernumber, String name) {
		return dao.prompt(customernumber, name);
	}


}
