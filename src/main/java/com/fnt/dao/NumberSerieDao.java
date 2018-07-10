package com.fnt.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.fnt.entity.NumberSerie;

@Stateless
public class NumberSerieDao {

	@PersistenceContext
	private EntityManager em;

	public NumberSerie create(NumberSerie numberSeries) {
		em.persist(numberSeries);
		return numberSeries;
	}


	public void delete(NumberSerie numberSeries) {
		em.remove(numberSeries);
	}

	public NumberSerie get(String id) {
		NumberSerie ret = em.find(NumberSerie.class, id);
		return ret;
	}

	public Boolean exists(String id) {
		NumberSerie ret = em.find(NumberSerie.class, id);
		return ret != null;
	}

	public int deleteAll() {
		Query query = em.createQuery("DELETE FROM NumberSeries");
		return query.executeUpdate();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Integer getNextNbr() {

		NumberSerie numberSerie = em.find(NumberSerie.class, "CUSTOMER_ORDER");
		Integer orderNumber = numberSerie.getValue();
		numberSerie.setValue(orderNumber + 1);
		em.flush();
		return orderNumber;

	}


	public void flush() {
		em.flush();
		
	}

}
