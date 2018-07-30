package com.fnt.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.fnt.entity.Lookup;
import com.fnt.entity.LookupPK;
import com.fnt.sys.AppException;

@Stateless
public class LookupService {

	private static final Integer HTTP_PRECONDITION_FAILED = 412;

	@PersistenceContext
	private EntityManager em;

	public Lookup create(Lookup record) {
		if (record == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "CustomCode cannot be null");
		}
		em.persist(record);
		return record;
	}

	public Lookup get(String constant, String code) {

		if (constant == null || constant.trim().length() < 1) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Invalid constant value");
		}
		if (code == null || code.trim().length() < 1) {
			throw new AppException(HTTP_PRECONDITION_FAILED,"Invalid code value");
		}
		LookupPK primaryKey = new LookupPK(constant.toUpperCase(), code);
		Lookup val = em.find(Lookup.class, primaryKey);
		return val;
	}

	public Lookup update(Lookup lookUp) {

		Lookup fetched = get(lookUp.getPrimaryKey().getConstant(), lookUp.getPrimaryKey().getCode());
		if (fetched != null) {
			fetched.setDescription(lookUp.getDescription());
			fetched.setDatadate(lookUp.getDatadate());
			fetched.setDatadouble(lookUp.getDatadouble());
			fetched.setDatalong(lookUp.getDatalong());
			fetched.setDatastr(lookUp.getDatastr());
		}
		return fetched;
	}

	public void delete(String constant, String code) {

		Lookup record = get(constant,code);
		if (record != null) {
			em.remove(record);
		}
	}

	public Boolean exists(String constant, String code) {

		try {
			Lookup record = get(constant,code);
			return record != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public List<Lookup> getAllFor(String constant) {
		if (constant == null || constant.trim().length() < 1) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Invalid constant value");
		}

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
