package in.pk.weather.util;

import java.util.Date;

public class DateUtils {
	
	// Empty private constructor to mention this is utility class not meant to create object.
	private DateUtils(){
		
	}
	
	public static Date getDate(long timestamp) {
		long timestampMillis = timestamp * 1000L;
		return new Date(timestampMillis);
	}

}
