package com.biswadahal.blog.rest;

import org.glassfish.hk2.api.Factory;

import com.biswadahal.blog.services.BlogUser;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class BlogUserInjector implements Factory<BlogUser>{

	@Override
	public BlogUser provide() {
		UserService userService = UserServiceFactory.getUserService();
		User googleUser = userService.getCurrentUser();
		if(googleUser != null){
			return new BlogUser(googleUser, userService.isUserAdmin());
		}else{
			return null;
		}
	}

	@Override
	public void dispose(BlogUser instance) {
		//no-op
	}

}
