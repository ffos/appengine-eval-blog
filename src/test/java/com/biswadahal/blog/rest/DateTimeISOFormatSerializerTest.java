package com.biswadahal.blog.rest;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DateTimeISOFormatSerializerTest extends AppEngineTests{
	
	@Test(dataProvider = "dates")
	public void correctSerializationToIso8601DateString(Date date, String expectedOutput) throws IOException{
		ObjectMapper om = injector.getInstance(ObjectMapper.class);
		String json = om.writeValueAsString(new Pojo(date));
		PojoString pojoString = om.readValue(json, PojoString.class);
		assertEquals(pojoString.date, expectedOutput);
	}
	
	@DataProvider(name="dates")
	private Object[][] iso8601DateStrings(){
		return new Object[][]{
			{null, null},
			//Below: Boundary condition - it's 2nd August at -04:00, which should be converted to UTC (3rd August)
			{new DateTime(2016, 8, 2, 20, 14, 33, DateTimeZone.forOffsetHours(-4)).toDate(), "2016-08-03T00:14:33+00:00"},
			//Below: it's 2nd August at UTC, which should be remain unchanged when converted (2nd August)
			{new DateTime(2016, 8, 2, 0, 14, 33, DateTimeZone.UTC).toDate(), "2016-08-02T00:14:33+00:00"}
		};
	}
	
	public static class Pojo {
		Date date;
		Pojo(){}
		Pojo(Date date){
			this.date = date;
		}
	}
	public static class PojoString {
		String date;
	}

}
