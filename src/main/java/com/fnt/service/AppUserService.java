package com.fnt.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.fnt.entity.AppUser;
import com.fnt.AppException;

@Stateless
public class AppUserService {

	private static final Integer HTTP_PRECONDITION_FAILED = 412;

	@PersistenceContext
	private EntityManager em;

	public AppUser store(String loggedinUser, AppUser user) {

		if (user == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity is null. Nothing to persist");
		}
		if (user.getLogin() == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Entity primary key must NOT be null at update");
		}
		if (!loggedinUser.equals(user.getLogin())) {
			throw new AppException(403, "Forbidden");
		}

		AppUser ret = em.find(AppUser.class, user.getLogin());
		if (ret == null) {
			em.persist(user);
			return user;
		} else {
			ret.setFirstname(user.getFirstname());
			ret.setLastname(user.getLastname());
			ret.setStreet(user.getStreet());
			ret.setPonr(user.getPonr());
			ret.setPadr(user.getPadr());
			ret.setPhone(user.getPhone());
			ret.setCountry(user.getCountry());
			return em.merge(ret);
		}
	}

	public AppUser get(String loggedinUser, String login) {
		if (login == null) {
			throw new AppException(HTTP_PRECONDITION_FAILED, "Login is null");
		}
		if (!loggedinUser.equals(login)) {
			throw new AppException(403, "Forbidden");
		}

		AppUser ret = em.find(AppUser.class, login);
		if(ret == null) {
			ret = new AppUser();
		}
		return ret;
	}

}
