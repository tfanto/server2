package com.fnt.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fnt.dao.CustomerDao;
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

	public List<Customer> getAll() {
		return dao.getAll();
	}

	public List<String> getAllCustomerIds() {
		return dao.getAllCustomerIds();
	}

	public int deleteAll() {
		return dao.deleteAll();
	}

}
