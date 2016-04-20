package com.biswadahal.blog.rest;

import java.io.IOException;

import javax.validation.ConstraintViolation;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ConstraintViolationSerializer extends StdSerializer<ConstraintViolation<?>> {

	private static final long serialVersionUID = 892375102341L;

	public ConstraintViolationSerializer() {
		super(ConstraintViolation.class, true);
	}

	@Override
	public void serialize(ConstraintViolation<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		if( value == null){
			jgen.writeStartObject();
			jgen.writeEndObject();
			return;
		}
		jgen.writeStartObject();
	    jgen.writeStringField("message", value.getMessage());
	    jgen.writeStringField("property", value.getPropertyPath() == null? null: value.getPropertyPath().toString());
	    jgen.writeEndObject();		
	}

}
