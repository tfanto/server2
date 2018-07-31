package com.fnt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fnt.dto.SearchData;
import com.fnt.entity.Item;
import com.fnt.entity.ItemView1;
import com.fnt.sys.AppException;

@Stateless
public class ItemService {

	private static final Integer HTTP_PRECONDITION_FAILED = 412;

	@PersistenceContext
	private EntityManager em;

	public Item create(Item item) {
		if (item == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		
		if(item.getInstock() == null ) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Instock cannot be null");			
		}
		
		if(item.getPrice() == null || item.getPrice() <= 0.0) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Price cannot be 0");			
		}
		
		if(item.getPurchaseprice() == null || item.getPurchaseprice() <= 0.0) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Purchaseprice cannot be 0");			
		}
		
		if(item.getOrderingpoint() == null || item.getOrderingpoint() <= 0) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Orderingpoint cannot be 0");			
		}

		
		em.persist(item);
		return item;
	}

	public Item update(Item item) {
		if (item == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		if (item.getId() == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity primary key must NOT be null at update");
		}
		
		if(item.getInstock() == null ) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Instock cannot be null");			
		}
		
		if(item.getPrice() == null || item.getPrice() <= 0.0) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Price cannot be 0");			
		}
		
		if(item.getPurchaseprice() == null || item.getPurchaseprice() <= 0.0) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Purchaseprice cannot be 0");			
		}
		
		if(item.getOrderingpoint() == null || item.getOrderingpoint() <= 0) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Orderingpoint cannot be 0");			
		}
		
		
		return em.merge(item);
	}

	public void delete(Long id) {
		if (id == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Id is null");
		}
		Item item = get(id);
		em.remove(item);
	}

	public void delete(Item item) {
		em.remove(item);
	}

	public Item get(Long id) {
		if (id == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Id is null");
		}
		Item ret = em.find(Item.class, id);
		return ret;
	}

	public Item getByItemNumber(String itemnumber) {
		TypedQuery<Item> query = em.createNamedQuery(Item.ITEM_GET_BY_ITEMNUMBER, Item.class);
		query.setParameter("itemnumber", itemnumber);
		try {
			Item obj = query.getSingleResult();
			return obj;
		} catch (NoResultException | NonUniqueResultException e) {
			return null;
		}
	}

	public List<String> getAllItemIds() {
		Query query = em.createQuery("SELECT i.itemnumber FROM Item i");
		@SuppressWarnings("unchecked")
		List<String> ids = query.getResultList();
		return ids;
	}

	public int deleteAll() {
		Query query = em.createQuery("DELETE FROM Item");
		return query.executeUpdate();
	}

	public List<ItemView1> getAllForOrdering() {

		Query query = em.createQuery("SELECT i FROM ItemView1 i", ItemView1.class);
		@SuppressWarnings("unchecked")
		List<ItemView1> rs = query.getResultList();
		return rs;
	}

	public List<Item> paginatesearch(Integer offset, Integer limit, String itemnumber, String description, String sortorder) {

		String sort = "";
		if (sortorder.length() > 0) {
			sortorder = sortorder.toLowerCase();
			sortorder = "u." + sortorder;
			sortorder = sortorder.replaceAll(",", ",u.");
			sort = " order by " + sortorder;
		}

		String where_and = " where ";
		String sql = "select u  from Item u ";
		Map<String, Object> params = new HashMap<>();

		if (itemnumber.length() > 0) {
			sql += where_and;

			if (itemnumber.indexOf("%") < 0) {
				sql += " u.itemnumber = :itemnumber";
			} else {
				sql += " u.itemnumber like :itemnumber";
			}
			params.put("itemnumber", itemnumber);
			where_and = " and ";
		}
		if (description.length() > 0) {
			sql += where_and;

			if (description.indexOf("%") < 0) {
				sql += " u.description = :description";
			} else {
				sql += " u.description like :description";
			}
			params.put("description", description);
			where_and = " and ";
		}

		sql += sort;
		TypedQuery<Item> query = em.createQuery(sql, Item.class);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		query.setFirstResult(offset);
		query.setMaxResults(limit);
		List<Item> rs = query.getResultList();
		return rs;

	}

	/*
	 * the total number of records in a paginated search must have the same search
	 * criteria as the paginated query (filter part)
	 */
	public Long paginatecount(String itemnumber, String description) {

		String where_and = " where ";
		String sql = "select count(u.id)  from Item u ";
		Map<String, Object> params = new HashMap<>();

		if (itemnumber.length() > 0) {
			sql += where_and;

			if (itemnumber.indexOf("%") < 0) {
				sql += " u.itemnumber = :itemnumber";
			} else {
				sql += " u.itemnumber like :itemnumber";
			}
			params.put("itemnumber", itemnumber);
			where_and = " and ";
		}
		if (description.length() > 0) {
			sql += where_and;

			if (description.indexOf("%") < 0) {
				sql += " u.description = :description";
			} else {
				sql += " u.description like :description";
			}
			params.put("description", description);
			where_and = " and ";
		}

		TypedQuery<Long> query = em.createQuery(sql, Long.class);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Long rs = query.getSingleResult();
		return rs;
	}

	public List<SearchData> prompt(String itemnumber, String description) {
		String where_and = " where ";
		String sql = "select u  from Item u ";
		Map<String, Object> params = new HashMap<>();

		if (itemnumber.length() > 0) {
			sql += where_and;

			if (itemnumber.indexOf("%") < 0) {
				sql += " u.itemnumber = :itemnumber";
			} else {
				sql += " u.itemnumber like :itemnumber";
			}
			params.put("itemnumber", itemnumber);
			where_and = " and ";
		}

		if (description.length() > 0) {
			sql += where_and;

			if (description.indexOf("%") < 0) {
				sql += " u.description = :description";
			} else {
				sql += " u.description like :description";
			}
			params.put("description", description);
			where_and = " and ";
		}

		sql += " order by u.itemnumber, u.description";

		TypedQuery<Item> query = em.createQuery(sql, Item.class);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		List<Item> tmpList = query.getResultList();
		List<SearchData> ret = new ArrayList<>();
		tmpList.forEach(item -> {
			ret.add(new SearchData(item.getItemnumber(), item.getDescription(), String.valueOf(item.getPrice())));
		});
		tmpList.clear();
		return ret;
	}

}
