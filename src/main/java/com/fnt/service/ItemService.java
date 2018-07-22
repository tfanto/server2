package com.fnt.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fnt.dao.ItemDao;
import com.fnt.dto.SearchData;
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

	public List<Long> getAllItemIds() {
		return dao.getAllItemIds();
	}

	public int deleteAll() {
		return dao.deleteAll();
	}

	public List<ItemView1> getAllForOrdering() {
		return dao.getAllForOrdering();
	}

	public List<Item> search(String itemnumber, String description,String sortorder) {



		return dao.search(itemnumber, description, sortorder);

	}

	// throws NumberFormatException if not an Integer
	Integer ensureInteger(String str) {
		if (str == null) {
			return 0;
		}
		if (str.length() < 1) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	// throws NumberFormatException if not an Integer
	Double ensureDouble(String str) {
		if (str == null) {
			return 0.0D;
		}
		if (str.length() < 1) {
			return 0.0D;
		}
		return Double.parseDouble(str);
	}

	public List<SearchData> prompt(String itemnumber, String description) {
		return dao.prompt( itemnumber, description);
	}

}
