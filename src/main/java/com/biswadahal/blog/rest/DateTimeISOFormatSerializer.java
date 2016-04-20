package com.biswadahal.blog.rest;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DateTimeISOFormatSerializer extends StdSerializer<Date> {

	private static final long serialVersionUID = 892394702341L;

	public DateTimeISOFormatSerializer() {
		super(Date.class, true);
	}

	@Override
	public void serialize(Date date, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		if( date == null){
			jgen.writeNull();
			return;
		}
		Date utcDate = UTCDateTime.toUtc(date);
		jgen.writeString(DateFormatUtils.formatUTC(utcDate, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern()));
	}

}
