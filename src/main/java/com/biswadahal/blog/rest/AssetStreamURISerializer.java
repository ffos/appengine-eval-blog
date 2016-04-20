package com.biswadahal.blog.rest;

import java.io.IOException;

import com.biswadahal.blog.models.Asset;
import com.biswadahal.blog.rest.ResourceEndpoints.Endpoints;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Optional;
import com.googlecode.objectify.Key;

public class AssetStreamURISerializer extends StdSerializer<Key<Asset>> {
	private static final long serialVersionUID = 11659981248559L;

	public AssetStreamURISerializer() {
		super(Key.class, true);
	}

	@Override
	public void serialize(Key<Asset> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		if (value == null) {
			return;
		}
		String kind = value.getKind();
		Optional<Endpoints> endpoints = ResourceEndpoints.get().endpointForKind(kind);
		if (endpoints.isPresent()) {
			jgen.writeString(String.format(endpoints.get().getById() + "/stream", value.getString()));
		}
	}
}
