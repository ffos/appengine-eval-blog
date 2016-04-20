package com.biswadahal.blog.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolation;

import com.biswadahal.blog.services.filters.Filter;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.cmd.Query;

/**
 * Package scope. Only intended to be used by services
 */
class FilteredQueryBuilder<T> {
	private List<ConstraintViolation<T>> violations = new ArrayList<>();
	private final QueryBuilder<T> qb;

	public FilteredQueryBuilder(Class<T> clazz) {
		qb = new QueryBuilder<>(clazz);
	}

	public Collection<ConstraintViolation<T>> getViolations() {
		return violations;
	}

	public Optional<Query<T>> buildFilterQuery(List<? extends Filter<T>> filters, int pageSize,
			int zeroBasedPageOffset) {
		Preconditions.checkNotNull(filters);
		Preconditions.checkArgument(!filters.isEmpty());
		
		Optional<Query<T>> qbQuery = qb.buildQuery(pageSize, zeroBasedPageOffset);
		if(!qbQuery.isPresent()){
			violations.addAll(qb.getViolations());
			for (Filter<T> f : filters) {
				if (f.hasErrors()) {
					violations.addAll(f.getErrors());
				}
			}
			return Optional.absent();
		}else {
			Query<T> query = qbQuery.get();
			for (Filter<T> f : filters) {
				query = query.filter(f.getCondition(), f.getValue());
			}
			return Optional.of(query);
		}
	}

}
