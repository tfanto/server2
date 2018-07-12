package com.fnt.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fnt.dao.ItemDao;
import com.fnt.entity.Item;
import com.fnt.entity.ItemView1;
import com.fnt.sys.AppException;

@Stateless
public class ItemService {

	private static final Integer HTTP_PRECONDITION_FAILED = 412;
	private static final Integer HTTP_NOT_FOUND = 404;

	@Inject
	private ItemDao dao;

	public Item create(Item item) {

		if (item == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		return dao.create(item);
	}

	public Item update(Item item) {

		if (item == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		if (item.getId() == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity primary key must NOT be null at update");
		}
		return dao.update(item);

	}

	public void delete(Long id) {

		Item service = get(id);
		dao.delete(service);
	}

	public Item get(Long id) {
		if (id == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Id is null");
		}
		Item fetched = dao.get(id);
		return fetched;
	}

	public List<Item> getAll() {
		return dao.getAll();
	}

	public List<Long> getAllItemIds() {
		return dao.getAllItemIds();
	}

	public int deleteAll() {
		return dao.deleteAll();
	}

	public List<ItemView1> getAllForOrdering() {
		return dao.getAllForOrdering();
	}

}
