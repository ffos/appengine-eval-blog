package com.biswadahal.blog.rest;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DateTimeISOFormatDeserializerTest extends AppEngineTests{
	
	@Test(dataProvider = "iso8601DateStrings")
	public void correctDeserializationOfIso8601DateString(String stringDate, Date expectedOutput) throws IOException{
		ObjectMapper om = injector.getInstance(ObjectMapper.class);
		String json = om.writeValueAsString(new PojoJson(stringDate));
		Pojo pojo = om.readValue(json, Pojo.class);
		assertEquals(pojo.date, expectedOutput);
	}
	
	@Test(dataProvider = "nonIso8601DateStrings", expectedExceptions=JsonMappingException.class)
	public void errorOnNonIso8601DateString(String stringDate) throws IOException{
		ObjectMapper om = injector.getInstance(ObjectMapper.class);
		String json = om.writeValueAsString(new PojoJson(stringDate));
		om.readValue(json, Pojo.class);
	}
	
	@DataProvider(name="iso8601DateStrings")
	private Object[][] iso8601DateStrings(){
		return new Object[][]{
			{null, null},
			//Below: Boundary condition - it's 2nd August at -04:00, which should be converted to UTC (3rd August)
			{"2016-08-02T20:14:33-04:00", new DateTime(2016, 8, 3, 0, 14, 33, DateTimeZone.UTC).toDate()},
			//Below: it's 2nd August at UTC, which should be remain unchanged when converted (2nd August)
			{"2016-08-02T00:14:33+00:00", new DateTime(2016, 8, 2, 0, 14, 33, DateTimeZone.UTC).toDate()}
		};
	}
	
	@DataProvider(name="nonIso8601DateStrings")
	private Object[][] nonIso8601DateStrings(){
		return new Object[][]{
			{"2016-08-02T00:14:33 00:00"},
			{"2016-08-02T20:14:33"},
			{"2016-08-02T00:14"},
			{"2016-08-02T00"},
			{"2016-08-02T"},
			{"2016-08-02"},
		};
	}
	
	public static class PojoJson {
		String date;
		PojoJson(){}
		PojoJson(String date){
			this.date = date;
		}
	}
	public static class Pojo {
		Date date;
	}
}
