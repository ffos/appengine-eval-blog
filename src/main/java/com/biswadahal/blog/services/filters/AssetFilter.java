package com.biswadahal.blog.services.filters;

import com.biswadahal.blog.models.Asset;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetFilter extends Filter<Asset> {
	@JsonCreator
	public AssetFilter(@JsonProperty("key") String key, @JsonProperty("op") String op,
			@JsonProperty("value") Object value) {
		super(Asset.class, "meta.", key, op, value);
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
