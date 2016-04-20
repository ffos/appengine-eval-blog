package com.biswadahal.blog.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolation;

import com.biswadahal.blog.dao.OfyService;
import com.google.common.base.Optional;
import com.googlecode.objectify.cmd.Query;

/**
 * Package scope. Only intended to be used by services
 */
class QueryBuilder<T> {
	private Class<T> clazz;
	private List<ConstraintViolation<T>> violations = new ArrayList<>();

	public QueryBuilder(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Collection<ConstraintViolation<T>> getViolations() {
		return violations;
	}

	public Optional<Query<T>> buildQuery(int pageSize, int zeroBasedPageOffset) {
		checkPageValues(pageSize, zeroBasedPageOffset);
		if (!violations.isEmpty()) {
			return Optional.absent();
		} else {
			Query<T> query = OfyService.ofy().load().type(clazz).limit(pageSize).offset(zeroBasedPageOffset);
			return Optional.of(query);
		}
	}

	private void checkPageValues(int pageSize, int zeroBasedPageOffset) {
		if (pageSize < 1 || zeroBasedPageOffset < 0) {
			if (pageSize < 1) {
				violations
						.add(new IllegalArgumentViolation<T>(String.format("Invalid page size: %s", pageSize), clazz));
			}
			if (zeroBasedPageOffset < 0) {
				violations.add(new IllegalArgumentViolation<T>(
						String.format("Invalid page offset (zero based): %s", zeroBasedPageOffset), clazz));
			}
		}
	}
}
