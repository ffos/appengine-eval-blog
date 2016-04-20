package com.biswadahal.blog.models.validation;

import com.googlecode.objectify.Key;

public class InvalidKey {
	private InvalidKey(){}
	
	public static Key<?> create(String value){
		return Key.create(InvalidKey.class, value);
	}
}
