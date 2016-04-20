package com.biswadahal.blog.services.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;

import com.biswadahal.blog.models.Tag;
import com.biswadahal.blog.services.IllegalArgumentViolation;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.googlecode.objectify.Key;

public abstract class Filter<T> {
	public enum FilterKey {
		META_TITLE("title"), 
		META_CAPTION("caption"), 
		META_MIMECLASSNAME("mimeType.className"), 
		META_MIMETYPENAME("mimeType.typeName"), 
		META_STATUS("status"), 
		META_LASTMODIFIEDTS("lastModifiedTimestamp"), 
		META_CREATEDTS("createdTimestamp"), 
		META_TAGS("tags"),
		META_ACCESS_CONTROL_PERM_OTHER("accessControl.other"),

		TAG_LABEL("label");
		
		private String propertyName;

		private FilterKey(String propName) {
			this.propertyName = propName;
		}

		public String getPropertyName() {
			return propertyName;
		}
	}

	@SuppressWarnings("serial")
	protected final Map<String, FilterKey> allowedFilterKeys = new TreeMap<String, FilterKey>() {
		{
			for (FilterKey key : FilterKey.values()) {
				put(key.toString().toLowerCase(), key);
			}
		}
	};

	@SuppressWarnings("serial")
	protected final Map<String, FilterOperator> allowedFilterOps = new TreeMap<String, FilterOperator>() {
		{
			for (FilterOperator op : FilterOperator.values()) {
				put(op.toString().toLowerCase(), op);
			}
		}
	};

	private FilterKey key;
	private FilterOperator operator;
	private Object value;
	private Class<T> clazz;
	private final List<ConstraintViolation<T>> errors = new ArrayList<>();
	private final String prefix;

	protected Filter(Class<T> clazz, String prefix) {
		this.clazz = clazz;
		this.prefix = prefix == null? "": prefix;
		configureAllowedKeys();
	}

	protected Filter(Class<T> clazz, String prefix, String key, String op, Object value) {
		this(clazz, prefix);
		setKey(key);
		setOperator(op);
		setValue(value);
	}

	protected abstract void configureAllowedKeys();

	protected void setKey(String key) {
		if (StringUtils.isBlank(key) || !allowedFilterKeys.keySet().contains(key.toLowerCase())) {
			errors.add(new IllegalArgumentViolation<T>(
					String.format("Filter key should be one of: %s", allowedFilterKeys.keySet()), clazz));
			return;
		}
		this.key = allowedFilterKeys.get(key.toLowerCase());
	}

	protected void setOperator(String op) {
		if (StringUtils.isBlank(op) || !allowedFilterOps.keySet().contains(op.toLowerCase())) {
			errors.add(new IllegalArgumentViolation<T>(
					String.format("Filter operator should be one of: %s", allowedFilterOps.keySet()), clazz));
			return;
		}
		this.operator = allowedFilterOps.get(op.toLowerCase());
	}

	protected void setValue(Object inputValue) {
		convertToKeyIfFilterKeyIsTags(inputValue);
	}

	protected void convertToKeyIfFilterKeyIsTags(Object inputValue) {
		if (FilterKey.META_TAGS.equals(key) && inputValue != null) {
			if (Collection.class.isAssignableFrom(inputValue.getClass())) {
				// convert value to Key type
				List<Key<Tag>> tags = new ArrayList<>();
				@SuppressWarnings("unchecked")
				Collection<Object> tagWebSafeKeys = (Collection<Object>) inputValue;
				for (Object o : tagWebSafeKeys) {
					if (o == null) {
						errors.add(new IllegalArgumentViolation<>("Tag key cannot be null in filter", clazz));
						continue;
					} else {
						try {
							Key<Tag> k = Key.create(o.toString());
							tags.add(k);
						} catch (IllegalArgumentException e) {
							errors.add(new IllegalArgumentViolation<>(String.format("Invalid Tag key: %s", o),
									clazz));
							continue;
						}
					}
				}
				value = tags;
			} else {
				errors.add(new IllegalArgumentViolation<>("Tags to filter with should be array", clazz));
			}
		}
	}

	public List<ConstraintViolation<T>> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public String getCondition() {
		return String.format("%s%s %s", prefix, key.propertyName, operator.toString());
	}
	public Object getValue() {
		return value;
	}
}
