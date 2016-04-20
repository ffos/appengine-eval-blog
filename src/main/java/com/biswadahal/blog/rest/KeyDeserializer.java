package com.biswadahal.blog.rest;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import com.biswadahal.blog.models.validation.InvalidKey;
import com.biswadahal.blog.rest.ResourceEndpoints.Endpoints;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;

public class KeyDeserializer extends StdDeserializer<Key<?>> {
	private static final long serialVersionUID = 491275419533L;

	public KeyDeserializer() {
		super(Key.class);
	}
	
	@Override
	public Key<?> getNullValue() {
		return InvalidKey.create("null");
	}

	@Override
	public Key<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return deserialize(jp.getText());
	}

	public Key<?> deserialize(String input) {
		if(StringUtils.isBlank(input)){
			return InvalidKey.create("null");
		}
		Optional<URI> uri = parseInputAsUri(input);
		Optional<Key<?>> retVal = Optional.absent();
		if(uri.isPresent()){
			String[] segments = uri.get().getPath().split("/");
			String keyString = segments[segments.length -1];
			retVal = parseAsWebsafeKey(keyString);
			if(retVal.isPresent() && !matchesKindResourceEndpointByPath(retVal.get(), uri.get())){
				retVal = Optional.absent();
			}
			
		}else{
			retVal = parseAsWebsafeKey(input);
		}
		if(retVal.isPresent()){
			return retVal.get();
		}else{
			return InvalidKey.create(String.format("%s", input));
		}
	}
	
	private boolean matchesKindResourceEndpointByPath(Key<?> key, URI uri){
		Optional<Endpoints> endpoints = ResourceEndpoints.get().endpointForKind(key.getKind());
		if(endpoints.isPresent()){
			String expectedUri = String.format(endpoints.get().getById(), key.getString());
			if(uri.toString().indexOf(expectedUri) >=0 ){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	private Optional<Key<?>> parseAsWebsafeKey(String input){
		try{
			return Optional.<Key<?>>of(Key.create(input));
		}catch(IllegalArgumentException e){
			return Optional.absent();
		}
	}
	
	private Optional<URI> parseInputAsUri(String input){
		Optional<URI> retVal = null;
		try{
			retVal = Optional.fromNullable(URI.create(input));
		}catch(IllegalArgumentException | NullPointerException e){
			retVal = Optional.absent();
		}
		return retVal;
	}
}
