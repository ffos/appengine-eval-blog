package com.biswadahal.blog.rest;

import java.io.IOException;

import com.biswadahal.blog.rest.ResourceEndpoints.Endpoints;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;

@SuppressWarnings("rawtypes")
public class KeySerializer extends StdSerializer<Key> {
	private static final long serialVersionUID = 11659981248559L;

	public KeySerializer() {
		super(Key.class);
	}

	@Override
	public void serialize(Key value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		if (value == null) {
			return;
		}

		String kind = value.getKind();
		Optional<Endpoints> endpoints = ResourceEndpoints.get().endpointForKind(kind);
		if (endpoints.isPresent()) {
			jgen.writeString(String.format(endpoints.get().getById(), value.getString()));
		} else {
			jgen.writeString(value.getString());
		}
	}
}
