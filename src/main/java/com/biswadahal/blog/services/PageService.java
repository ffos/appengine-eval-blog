package com.biswadahal.blog.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.models.Page;
import com.biswadahal.blog.services.filters.AccessControlFilter;
import com.biswadahal.blog.services.filters.PageFilter;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

public class PageService {
	@Inject
	private Validator validator;

	public ServiceResult<Page> save(Page page, Class<?>... validationGroups) {
		ValidationGroupsUtil.checkArguments(validationGroups);
		if (page == null) {
			Collection<ConstraintViolation<Page>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("Page to save is missing", Page.class));
			return new ServiceResult<Page>(null, noInputViolation);
		}
		ServiceResult<Page> result = null;
		if (page.getMeta() != null) {
			page.getMeta().updateLastModifiedTimestampToCurrentTime();
		}
		Set<ConstraintViolation<Page>> violations = validator.validate(page, validationGroups);
		if (!violations.isEmpty()) {
			result = new ServiceResult<Page>(null, violations);
		} else {
			OfyService.ofy().save().entities(page).now();
			result = new ServiceResult<Page>(page);
		}
		return result;
	}
	
	public ServiceResult<Page> findByKey(Key<Page> key, Optional<BlogUser> blogUser ){
		if (key == null) {
			Collection<ConstraintViolation<Page>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("Page key to get is missing", Page.class));
			return new ServiceResult<Page>(null, noInputViolation);
		}
		Query<Page> query = OfyService.ofy().load().type(Page.class).filterKey(key);
		AccessControlFilter<Page> accessControlFilter = new AccessControlFilter<>(query, blogUser, "meta.");
		query = accessControlFilter.apply();
		ServiceResult<Page> result = new ServiceResult<Page>(query.first().now());
		return result;
	}

	public ServiceListResult<Page> filter(int pageSize, int zeroBasedPageOffset, Optional<List<PageFilter>> filters, Optional<BlogUser> blogUser) {
		Optional<Query<Page>> optQuery = null; 
		Collection<ConstraintViolation<Page>> violations = Collections.emptyList();
		if (filters.isPresent() && !filters.get().isEmpty()){
			FilteredQueryBuilder<Page> queryBuilder = new FilteredQueryBuilder<>(Page.class);
			optQuery = queryBuilder.buildFilterQuery(filters.get(), pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}else{
			QueryBuilder<Page> queryBuilder = new QueryBuilder<>(Page.class);
			optQuery = queryBuilder.buildQuery(pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}
		if(!violations.isEmpty()){
			return new ServiceListResult<Page>(null, violations);
		}else{
			Query<Page> query = optQuery.get();
			AccessControlFilter<Page> accessControlFilter = new AccessControlFilter<>(query, blogUser, "meta.");
			query = accessControlFilter.apply();
			query.order("-lastModifiedTimestamp");
			ServiceListResult<Page> result = new ServiceListResult<Page>(query.list());
			return result;
		}
	}

	
}