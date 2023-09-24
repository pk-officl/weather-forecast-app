package in.pk.weather.constant;

public class WeatherConstants {
	public class DateTime {
		public static final String SIMPLE_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS Z";
	}

	public static final String COORDINATE_SPLITTER = ",";
	public static final String WEATHER_SPLITTER = ":";
	public static final String WIND_SPLITTER = "/";
	
	public static final String WEATHER_TYPE_CURRENT = "current";
	public static final String WEATHER_TYPE_FORECAST = "forecast";
	
	public static final String WEATHER_IN_HOUR_THREE = "3h";
	
	public static final int SECONDS_FOR_ONE_HOUR = 3600;
	public static final int SECONDS_FOR_ONE_DAY = SECONDS_FOR_ONE_HOUR * 24;
	
	
}
