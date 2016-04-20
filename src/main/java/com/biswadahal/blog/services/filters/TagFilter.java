package com.biswadahal.blog.services.filters;

import com.biswadahal.blog.models.Tag;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TagFilter extends Filter<Tag> {
	@JsonCreator
	public TagFilter(@JsonProperty("key") String key, @JsonProperty("op") String op,
			@JsonProperty("value") Object value) {
		super(Tag.class, "", key, op, value);
	}

	@Override
	protected void configureAllowedKeys() {
		allowedFilterKeys.clear();
		allowedFilterKeys.put(FilterKey.TAG_LABEL.getPropertyName(), FilterKey.TAG_LABEL);
	}

}
