package com.fnt.sys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.SecurityContext;

import com.fnt.AppException;

@Stateless
public class AppCommonLib {

	public AppCommonLib() {

	}

	public void isAllowed(SecurityContext ctx, String role) {
		if (ctx == null) {
			throw new IllegalAccessError("Not allowed");
		}
		if (role == null) {
			throw new IllegalAccessError("Not allowed");
		}
		if (!ctx.isUserInRole(role)) {
			throw new IllegalAccessError("Not allowed");
		}
	}

	public <T> void validate(T obj) {

		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
		if (constraintViolations.size() > 0) {
			Set<String> violationMessages = new HashSet<>();
			for (ConstraintViolation<T> constraintViolation : constraintViolations) {
				violationMessages.add(constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage());
			}
			throw new AppException(412, violationMessages);
		}
	}
	

}
