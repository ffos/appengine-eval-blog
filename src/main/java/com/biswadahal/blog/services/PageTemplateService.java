package com.biswadahal.blog.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.services.filters.AccessControlFilter;
import com.biswadahal.blog.services.filters.PageTemplateFilter;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

public class PageTemplateService {
	@Inject
	private Validator validator;

	public ServiceResult<PageTemplate> save(PageTemplate template, Class<?>... validationGroups) {
		ValidationGroupsUtil.checkArguments(validationGroups);
		if (template == null) {
			Collection<ConstraintViolation<PageTemplate>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("PageTemplate to save is missing", PageTemplate.class));
			return new ServiceResult<PageTemplate>(null, noInputViolation);
		}
		ServiceResult<PageTemplate> result = null;
		if (template.getMeta() != null) {
			template.getMeta().updateLastModifiedTimestampToCurrentTime();
		}
		Set<ConstraintViolation<PageTemplate>> violations = validator.validate(template, validationGroups);
		if (!violations.isEmpty()) {
			result = new ServiceResult<PageTemplate>(null, violations);
		} else {
			OfyService.ofy().save().entities(template).now();
			result = new ServiceResult<PageTemplate>(template);
		}
		return result;
	}
	
	public ServiceResult<PageTemplate> findByKey(Key<PageTemplate> key, Optional<BlogUser> blogUser){
		if (key == null) {
			Collection<ConstraintViolation<PageTemplate>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("PageTemplate to get is missing", PageTemplate.class));
			return new ServiceResult<PageTemplate>(null, noInputViolation);
		}
		Query<PageTemplate> query = OfyService.ofy().load().type(PageTemplate.class).filterKey(key);
		AccessControlFilter<PageTemplate> accessControlFilter = new AccessControlFilter<>(query, blogUser, "meta.");
		query = accessControlFilter.apply();
		ServiceResult<PageTemplate> result = new ServiceResult<PageTemplate>(query.first().now());
		return result;
	}
	
	public ServiceListResult<PageTemplate> filter(int pageSize, int zeroBasedPageOffset, Optional<List<PageTemplateFilter>> filters, Optional<BlogUser> blogUser) {
		Optional<Query<PageTemplate>> optQuery = null; 
		Collection<ConstraintViolation<PageTemplate>> violations = Collections.emptyList();
		if (filters.isPresent() && !filters.get().isEmpty()){
			FilteredQueryBuilder<PageTemplate> queryBuilder = new FilteredQueryBuilder<>(PageTemplate.class);
			optQuery = queryBuilder.buildFilterQuery(filters.get(), pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}else{
			QueryBuilder<PageTemplate> queryBuilder = new QueryBuilder<>(PageTemplate.class);
			optQuery = queryBuilder.buildQuery(pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}
		if(!violations.isEmpty()){
			return new ServiceListResult<PageTemplate>(null, violations);
		}else{
			Query<PageTemplate> query = optQuery.get();
			AccessControlFilter<PageTemplate> accessControlFilter = new AccessControlFilter<>(query, blogUser, "meta.");
			query = accessControlFilter.apply();
			query.order("-lastModifiedTimestamp");
			ServiceListResult<PageTemplate> result = new ServiceListResult<PageTemplate>(query.list());
			return result;
		}
	}
	
}