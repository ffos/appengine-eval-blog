package com.biswadahal.blog.models;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.googlecode.objectify.annotation.Index;

/**
 * Admin has all permission always, others can have different permissions
 */
@Index
public class AccessControl {
	public enum Permission {
		VIEW, EDIT, DELETE
	}

	/**
	 * Similar to "other" in unix file permission. There is no group or
	 * discretionary access for now Keeping things simpler
	 */
	@NotNull
	private Set<Permission> other = new HashSet<>();

	public AccessControl() {
		// Needed by Objectify
	}

	public AccessControl(Set<Permission> other) {
		super();
		this.other = other;
	}

	public Set<Permission> getOther() {
		return other;
	}

	public void setOther(Set<Permission> other) {
		this.other = other;
	}
}
