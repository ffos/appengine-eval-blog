package com.biswadahal.blog.services.filters;

import com.biswadahal.blog.models.PageTemplate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PageTemplateFilter extends Filter<PageTemplate> {
	@JsonCreator
	public PageTemplateFilter(@JsonProperty("key") String key, @JsonProperty("op") String op,
			@JsonProperty("value") Object value) {
		super(PageTemplate.class, "meta.", key, op, value);
	}

	@Override
	protected void configureAllowedKeys() {
		allowedFilterKeys.clear();
		allowedFilterKeys.put(FilterKey.META_STATUS.getPropertyName(), FilterKey.META_STATUS);
		allowedFilterKeys.put(FilterKey.META_LASTMODIFIEDTS.getPropertyName(), FilterKey.META_LASTMODIFIEDTS);
		allowedFilterKeys.put(FilterKey.META_CREATEDTS.getPropertyName(), FilterKey.META_CREATEDTS);
		allowedFilterKeys.put(FilterKey.META_TAGS.getPropertyName(), FilterKey.META_TAGS);
	}

}
