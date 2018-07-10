package com.fnt.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.fnt.dao.LookupDao;
import com.fnt.entity.Lookup;
import com.fnt.entity.LookupPK;

@Stateless
public class LookupService {

	@Inject
	private LookupDao dao;

	public Lookup create(Lookup record) {

		if (record == null) {
			throw new IllegalArgumentException("CustomCode cannot be null");
		}

		return dao.create(record);

	}

	public Lookup get(String constant, String code) {

		if (constant == null) {
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if (constant.trim().length() < 1) {
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if (code == null) {
			throw new IllegalArgumentException("Code cannot be null");
		}
		if (code.trim().length() < 1) {
			throw new IllegalArgumentException("Code cannot be empty");
		}
		LookupPK primaryKey = new LookupPK(constant.toUpperCase(), code);
		return dao.get(primaryKey);
	}

	public Lookup get(LookupPK primaryKey) {

		if (primaryKey == null) {
			throw new IllegalArgumentException("CustomCodesPk cannot be null");
		}
		return dao.get(primaryKey);
	}

	public Boolean exists(String constant, String code) {

		if (constant == null) {
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if (constant.trim().length() < 1) {
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if (code == null) {
			throw new IllegalArgumentException("Code cannot be null");
		}
		if (code.trim().length() < 1) {
			throw new IllegalArgumentException("Code cannot be empty");
		}

		LookupPK primaryKey = new LookupPK(constant.toUpperCase(), code);
		return dao.exists(primaryKey);

	}

	public Lookup update(Lookup lookup) {

		return dao.update(lookup);

	}

	public void delete(String constant, String code) {
		if (constant == null) {
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if (constant.trim().length() < 1) {
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		if (code == null) {
			throw new IllegalArgumentException("Code cannot be null");
		}
		if (code.trim().length() < 1) {
			throw new IllegalArgumentException("Code cannot be empty");
		}
		LookupPK primaryKey = new LookupPK(constant.toUpperCase(), code);
		dao.delete(primaryKey);

	}

	public List<Lookup> getAllFor(String constant) {
		if (constant == null) {
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if (constant.trim().length() < 1) {
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		return dao.getAllFor(constant.toUpperCase());

	}

	public void deleteAllFor(String constant) {
		if (constant == null) {
			throw new IllegalArgumentException("Constant cannot be null");
		}
		if (constant.trim().length() < 1) {
			throw new IllegalArgumentException("Constant cannot be empty");
		}
		dao.deleteAllFor(constant.toUpperCase());

	}

	public List<String> getAllConstants() {

		return dao.getAllConstants();

	}

}