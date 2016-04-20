package com.biswadahal.blog.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.models.Tag;
import com.biswadahal.blog.services.filters.TagFilter;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

@Singleton
public class TagService {
	@Inject
	private Validator validator;

	public ServiceResult<Tag> save(Tag tag, Class<?>... validationGroups) {
		ValidationGroupsUtil.checkArguments(validationGroups);
		if (tag == null) {
			Collection<ConstraintViolation<Tag>> noInputViolation = ViolationBuilder
					.wrapInCollection(new IllegalArgumentViolation<>("Tag to save is missing", Tag.class));
			return new ServiceResult<Tag>(null, noInputViolation);
		}
		ServiceResult<Tag> result = null;
		Set<ConstraintViolation<Tag>> violations = validator.validate(tag, validationGroups);
		if (!violations.isEmpty()) {
			result = new ServiceResult<Tag>(null, violations);
		} else {
			result = findByLabel(tag.getLabel());
			if (!result.getValue().isPresent()) {
				OfyService.ofy().save().entities(tag).now();
				result = new ServiceResult<Tag>(tag);
			}
		}
		return result;
	}
	
	public ServiceListResult<Tag> filter(int pageSize, int zeroBasedPageOffset, Optional<List<TagFilter>> filters, Optional<BlogUser> blogUser) {
		Optional<Query<Tag>> optQuery = null; 
		Collection<ConstraintViolation<Tag>> violations = Collections.emptyList();
		if (filters.isPresent() && !filters.get().isEmpty()){
			FilteredQueryBuilder<Tag> queryBuilder = new FilteredQueryBuilder<>(Tag.class);
			optQuery = queryBuilder.buildFilterQuery(filters.get(), pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}else{
			QueryBuilder<Tag> queryBuilder = new QueryBuilder<>(Tag.class);
			optQuery = queryBuilder.buildQuery(pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}
		if(!violations.isEmpty()){
			return new ServiceListResult<Tag>(null, violations);
		}else{
			Query<Tag> query = optQuery.get();
			ServiceListResult<Tag> result = new ServiceListResult<Tag>(query.list());
			return result;
		}
	}
	
	
	public ServiceResult<Tag> findByKey(Key<Tag> key, Optional<BlogUser> blogUser ){
		if (key == null) {
			Collection<ConstraintViolation<Tag>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("Tag key to get is missing", Tag.class));
			return new ServiceResult<Tag>(null, noInputViolation);
		}
		Query<Tag> query = OfyService.ofy().load().type(Tag.class).filterKey(key);
		ServiceResult<Tag> result = new ServiceResult<Tag>(query.first().now());
		return result;
	}
	

	public ServiceResult<Tag> findByLabel(String label) {
		if (label == null || label.trim().length() < 1) {
			return new ServiceResult<Tag>(null);
		}
		String queryLabel = label.toLowerCase(); // FIXME: Not locale aware
		Tag retVal = OfyService.ofy().load().type(Tag.class).filter("label", queryLabel).first().now();
		return new ServiceResult<Tag>(retVal);
	}

}
