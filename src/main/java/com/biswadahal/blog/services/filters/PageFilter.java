package com.biswadahal.blog.services.filters;

import com.biswadahal.blog.models.Page;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PageFilter extends Filter<Page> {
	@JsonCreator
	public PageFilter(@JsonProperty("key") String key, @JsonProperty("op") String op,
			@JsonProperty("value") Object value) {
		super(Page.class, "meta.", key, op, value);
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
