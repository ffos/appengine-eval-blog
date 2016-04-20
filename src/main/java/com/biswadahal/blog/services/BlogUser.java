package com.biswadahal.blog.services;

import com.google.appengine.api.users.User;
import com.google.common.base.Preconditions;

public class BlogUser {
	private User googleUser;
	private boolean isAdmin;
	
	public BlogUser(User googleUser, boolean userIsAdmin){
		Preconditions.checkNotNull(googleUser);
		this.googleUser = googleUser;
		this.isAdmin = userIsAdmin;
	}

	public User getGoogleUser() {
		return googleUser;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public String toString() {
		return "BlogUser [googleUser=" + googleUser + ", isAdmin=" + isAdmin + "]";
	}
	
	
}
