package com.biswadahal.blog.rest;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public abstract class UTCDateTime {
	
	public static Date now(){
		return new DateTime(DateTimeZone.UTC).toDate();
	}
	
	public static Date toUtc(Date date){
		DateTime dateTime = new DateTime(date);
		Date utcDate = dateTime.toDateTime(DateTimeZone.UTC).toDate();
		return utcDate;
	}

}
