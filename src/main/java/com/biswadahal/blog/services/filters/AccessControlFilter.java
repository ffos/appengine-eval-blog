package com.biswadahal.blog.services.filters;

import com.biswadahal.blog.models.AccessControl;
import com.biswadahal.blog.services.BlogUser;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.cmd.Query;

public class AccessControlFilter<T> {
	private final Query<T> query;
	private final String propertyPrefix;
	private final Optional<BlogUser> user;

	public AccessControlFilter(Query<T> query, Optional<BlogUser> user, String propertyPrefix) {
		Preconditions.checkNotNull(query);
		Preconditions.checkNotNull(user);
		this.query = query;
		this.user = user;
		this.propertyPrefix = propertyPrefix == null ? "" : propertyPrefix;
	}

	public Query<T> apply() {
		if (!user.isPresent() || !user.get().isAdmin()) {
			// restrict by permission if not admin user
			String condition = String.format("%s%s", propertyPrefix,
					Filter.FilterKey.META_ACCESS_CONTROL_PERM_OTHER.getPropertyName());
			return query.filter(condition, AccessControl.Permission.VIEW);
		} else {
			return query;
		}
	}
}
