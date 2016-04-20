package com.biswadahal.blog.rest;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.appengine.api.datastore.Text;

public class DatastoreTextDeserializer extends StdDeserializer<Text> {

	private static final long serialVersionUID = 892375102341L;

	public DatastoreTextDeserializer() {
		super(Text.class);
	}

	@Override
	public Text deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String txt = p.readValueAs(String.class);
		if(StringUtils.isNotBlank(txt)){
			return new Text(txt);
		}else{
			return null;
		}
	}

}
