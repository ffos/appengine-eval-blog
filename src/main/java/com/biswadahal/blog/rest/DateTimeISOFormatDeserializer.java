package com.biswadahal.blog.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DateTimeISOFormatDeserializer extends StdDeserializer<Date> {

	private static final long serialVersionUID = 892375102341L;

	public DateTimeISOFormatDeserializer() {
		super(Date.class);
	}

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String txt = p.readValueAs(String.class);
		if(StringUtils.isNotBlank(txt)){
			Date parsedDate;
			try{
				parsedDate = FastDateFormat.getInstance(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern()).parse(txt);
			}catch(ParseException pe){
				throw ctxt.weirdStringException(txt, Date.class, pe.getMessage());
			}
			return parsedDate;
		}else{
			return null;
		}
	}

}
