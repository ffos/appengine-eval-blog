package com.biswadahal.blog.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.appengine.api.datastore.Text;

public class DatastoreTextSerializer extends StdSerializer<Text> {

	private static final long serialVersionUID = 892375102341L;

	public DatastoreTextSerializer() {
		super(Text.class, true);
	}

	@Override
	public void serialize(Text text, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		if( text == null){
			jgen.writeNull();
			return;
		}
		jgen.writeString(text.getValue());
	}

}
