package com.biswadahal.blog.dao;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.objectify.ObjectifyService;

public class OfyService {
	public static final Logger logger = LoggerFactory.getLogger(OfyService.class);
	
	@Inject
	public static void setObjectifyFactory(OfyFactory factory){
		logger.info(String.format("Set custom ObjectifyService factory: %s", OfyFactory.class));
		ObjectifyService.setFactory(factory);
	}
	
	public static Ofy ofy(){
		Ofy ofy = (Ofy)ObjectifyService.ofy();
		logger.trace(String.format("Return ofy instance: %s", ofy));
		return ofy;
	}
	
	public static OfyFactory factory(){
		return (OfyFactory) ObjectifyService.factory();
	}

}
