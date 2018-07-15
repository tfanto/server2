package com.fnt.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fnt.entity.Item;
import com.fnt.entity.ItemView1;
import com.fnt.sys.AppException;

@Stateless
public class ItemDao {

	@PersistenceContext
	private EntityManager em;

	public Item create(Item item) {
		em.persist(item);
		return item;
	}

	public Item update(Item item) {
		return em.merge(item);
	}

	public void delete(Item item) {
		em.remove(item);
	}

	public Item get(Long id) {
		Item ret = em.find(Item.class, id);
		return ret;
	}

	public List<Long> getAllItemIds() {
		Query query = em.createQuery("SELECT i.id FROM Item i");
		@SuppressWarnings("unchecked")
		List<Long> ids = query.getResultList();
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

	public List<Item> search(String itemnumber, String description, String sortorder) {

		String sort = "";
		if (sortorder.length() > 0 ) {
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

		List<Item> rs = query.getResultList();

		if (rs.size() > 2500) {
			throw new AppException(400, "To many rows (2500) in resultset. Please refine your search criteria");
		}
		return rs;

	}

}
