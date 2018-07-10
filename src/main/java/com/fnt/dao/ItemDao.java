package com.fnt.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fnt.entity.Item;
import com.fnt.entity.ItemView1;

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

	public Item get(String id) {
		Item ret = em.find(Item.class, id);
		return ret;
	}
	
	public Boolean exists(String id) {
		Item ret = em.find(Item.class, id);
		return ret != null;
	}

	public List<Item> getAll() {
		TypedQuery<Item> query = em.createNamedQuery(Item.ITEM_GET_ALL, Item.class);
		return query.getResultList();
	}
	
	public List<String> getAllItemIds() {
		Query query = em.createQuery("SELECT i.id FROM Item i");
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


}
