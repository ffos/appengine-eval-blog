package com.biswadahal.blog.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.objectify.impl.ObjectifyImpl;

public class Ofy extends ObjectifyImpl<Ofy>{
	public static final Logger logger = LoggerFactory.getLogger(Ofy.class);

	public Ofy(OfyFactory base){
		super(base);
		logger.trace(String.format("Instantiated custom ObjectifyImpl: %s using base: %s",this, base));
	}

}
