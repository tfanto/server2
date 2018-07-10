package com.fnt.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.fnt.entity.Lookup;
import com.fnt.entity.LookupPK;

@Stateless
public class LookupDao {

	@PersistenceContext
	private EntityManager em;

	public Lookup create(Lookup record) {
		em.persist(record);
		return record;
	}

	public Lookup get(LookupPK primaryKey) {
		try {
			Lookup val = em.find(Lookup.class, primaryKey);
			return val;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public Lookup update(Lookup lookUp) {

		Lookup fetched = get(lookUp.getPrimaryKey());
		if (fetched != null) {			
			fetched.setDescription(lookUp.getDescription());
			fetched.setDatadate(lookUp.getDatadate());
			fetched.setDatadouble(lookUp.getDatadouble());
			fetched.setDatalong(lookUp.getDatalong());
			fetched.setDatastr(lookUp.getDatastr());			
		}
		return fetched;
	}

	public void delete(LookupPK primaryKey) {

		Lookup record = get(primaryKey);
		if (record != null) {
			em.remove(record);
		}
	}

	public Boolean exists(LookupPK primaryKey) {

		try {
			Lookup record = get(primaryKey);
			return record != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public List<Lookup> getAllFor(String constant) {
		try {
			TypedQuery<Lookup> query = em.createNamedQuery(Lookup.SYSVAL_GETALLFOR, Lookup.class);
			query.setParameter("constant", constant);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void deleteAllFor(String constant) {

		List<Lookup> rs = getAllFor(constant);
		if (rs != null) {
			for (Lookup customCode : rs) {
				em.remove(customCode);
			}
		}
	}

	public List<String> getAllConstants() {
		try {
			TypedQuery<String> query = em.createNamedQuery(Lookup.SYSVAL_GETALLCONSTANTS, String.class);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
