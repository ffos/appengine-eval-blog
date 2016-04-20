package com.biswadahal.blog.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class ViolationBuilder {

	@SafeVarargs
	public static <T> Collection<ConstraintViolation<T>> wrapInCollection(ConstraintViolation<T>... constraintViolations) {
		if (constraintViolations == null) {
			return Collections.emptySet();
		}
		Set<ConstraintViolation<T>> violations = new HashSet<>();
		for (ConstraintViolation<T> v : constraintViolations) {
			if (v != null) {
				violations.add(v);
			}
		}
		return violations;
	}

}
