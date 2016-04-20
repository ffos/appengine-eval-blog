package com.biswadahal.blog.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;

public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
	@JsonInclude(Include.NON_NULL)
	public static class ParseError {
		String type = "Json Parse Error";
		String message;
		Integer lineNumber;
		Integer columnNumber;
		Long byteOffset;
	}

	@Override
	public Response toResponse(JsonParseException exception) {
		ParseError err = new ParseError();
		JsonLocation location = exception.getLocation();
		if (exception.getLocation() != null) {
			err.lineNumber = location.getLineNr();
			err.columnNumber = location.getColumnNr();
			err.byteOffset = location.getByteOffset();
		}
		err.message = exception.getOriginalMessage();
		return Response.status(Status.BAD_REQUEST).entity(err).type(MediaType.APPLICATION_JSON).build();
	}

}
