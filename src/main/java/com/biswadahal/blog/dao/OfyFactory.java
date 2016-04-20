package com.biswadahal.blog.dao;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.models.Asset;
import com.biswadahal.blog.models.Blog;
import com.biswadahal.blog.models.HelloEntity;
import com.biswadahal.blog.models.Page;
import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.Tag;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

@Singleton
public class OfyFactory extends ObjectifyFactory{
	public static final Logger logger = LoggerFactory.getLogger(OfyFactory.class);
	
	private Injector injector;
	
	private void registerEntities(){
		logger.trace("Register Objectify Entities");
		this.register(HelloEntity.class);
		this.register(Asset.class);
		this.register(Blog.class);
		this.register(Page.class);
		this.register(PageTemplate.class);
		this.register(Tag.class);
	}


	@Inject
	public OfyFactory(Injector injector){
		this.injector = injector;
		registerEntities();
	}
	
	@Override
	public <T> T construct(Class<T> type) {
		return injector.getInstance(type);
	}

	@Override
	public Objectify begin() {
		return new Ofy(this);
	}
	
	
}
