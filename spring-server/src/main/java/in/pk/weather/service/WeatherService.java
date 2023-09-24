package in.pk.weather.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.pk.weather.constant.WeatherConstants;
import in.pk.weather.manager.WeatherApiManager;
import in.pk.weather.model.LocationTO;
import in.pk.weather.model.WeatherDataTO;
import in.pk.weather.repository.LocationRepository;
import in.pk.weather.repository.WeatherDataRepository;
import in.pk.weather.util.DateUtils;

@Service
public class WeatherService
{

	private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

	@Autowired
	private WeatherApiManager weatherApiManager;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private WeatherDataRepository weatherDataRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Value("${weather.update.period}")
	private Long weatherUpdatePeriod;

	/**
	 * Method to get weather forecast data based on the location
	 * 
	 * @param location,
	 *            The location for which weather data needed
	 * @param weatherType,
	 *            The type to choose whether as current weather or forecast
	 * @return JSON Object of weather details for the given location
	 * @author Praveen.K
	 * @param weatherType
	 * @since 1.0
	 */
	public JSONObject getWeather(String location, String weatherType) {
		log.info("Entering into getWeather with location : {}", location);
		JSONObject resJson = new JSONObject();
		try {
			if (StringUtils.isNotBlank(location)) {
				if (StringUtils.isBlank(weatherType)) {
					weatherType = WeatherConstants.WEATHER_TYPE_CURRENT;
				}
				List<WeatherDataTO> weatherDataTOs = getWeatherData(location, weatherType);
				if (!weatherDataTOs.isEmpty()) {
					if (WeatherConstants.WEATHER_TYPE_CURRENT.equalsIgnoreCase(weatherType)) {
						JSONObject weatherDataJson = parseWeatherJson(weatherDataTOs.get(0));
						resJson.put(weatherType, weatherDataJson);
					} else {
						List<JSONObject> weatherDataList = new ArrayList<>();
						for (WeatherDataTO weatherDataTO : weatherDataTOs) {
							weatherDataList.add(parseWeatherJson(weatherDataTO));
						}
						resJson.put(weatherType, weatherDataList);
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception occurred in getWeatherForecast...", e);
		}
		return resJson;
	}

	/***
	 * Method to parse the Table object to JSON Object
	 * 
	 * @param weatherDataTO
	 * @return weatherDataJson, parsed JSON Object
	 * @throws JSONException
	 * @author Pravee.K
	 * @since 1.0
	 */
	private JSONObject parseWeatherJson(WeatherDataTO weatherDataTO) throws JSONException {
		try {
			JSONObject weatherDataJson = new JSONObject();
			weatherDataJson.put("weather", weatherDataTO.getWeather().substring(0, weatherDataTO.getWeather().lastIndexOf(WeatherConstants.WEATHER_SPLITTER)));
			weatherDataJson.put("description", weatherDataTO.getWeather().substring(weatherDataTO.getWeather().lastIndexOf(WeatherConstants.WEATHER_SPLITTER) + 1, weatherDataTO.getWeather().length()));
			weatherDataJson.put("date", weatherDataTO.getDate());
			weatherDataJson.put("temperature", weatherDataTO.getTemperature());
			weatherDataJson.put("temperature_min", weatherDataTO.getTemperatureMin());
			weatherDataJson.put("temperature_max", weatherDataTO.getTemperatureMax());
			weatherDataJson.put("humidity", weatherDataTO.getHumidity());
			weatherDataJson.put("pressure", weatherDataTO.getPressure());
			String wind = weatherDataTO.getWind();
			if (StringUtils.isNotBlank(wind)) {
				weatherDataJson.put("wind_speed", wind.substring(0, wind.lastIndexOf(WeatherConstants.WIND_SPLITTER)));
				weatherDataJson.put("wind_deg", wind.substring(wind.lastIndexOf(WeatherConstants.WIND_SPLITTER) + 1, wind.length()));
			}
			String rain = weatherDataTO.getRain();
			if (StringUtils.isNotBlank(rain)) {
				weatherDataJson.put("rain_hour", rain.substring(0, rain.lastIndexOf(WeatherConstants.WEATHER_SPLITTER)));
				weatherDataJson.put("rain_level", rain.substring(rain.lastIndexOf(WeatherConstants.WEATHER_SPLITTER) + 1, rain.length()));
			}
			weatherDataJson.put("date", weatherDataTO.getDate());
			weatherDataJson.put("location", weatherDataTO.getLocation().getLocationName());
			weatherDataJson.put("coordinates", weatherDataTO.getLocation().getCoordinates());
			weatherDataJson.put("country", weatherDataTO.getLocation().getCountry());
			return weatherDataJson;
		} catch (JSONException e) {
			log.error("Exception occurred in parseWeatherJson...", e);
		}
		return null;
	}

	/***
	 * Method to get weather data from database or API based on the date
	 * 
	 * @param location
	 * @param weatherType
	 * @return weatherDataTOs, list of WeatherDataTO
	 * @throws JSONException
	 * @throws ParseException
	 * @author Praveen.K
	 * @since 1.0
	 */
	private List<WeatherDataTO> getWeatherData(String location, String weatherType) throws JSONException, ParseException {
		List<WeatherDataTO> weatherDataTOs = new ArrayList<>();
		try {
			Date maxDate = getMaxDateByLocation(location, weatherType);
			long currentTimeStamp = System.currentTimeMillis();
			long timeStampRequired = (maxDate != null ? maxDate.getTime() : 0l) + (weatherUpdatePeriod != null ? weatherUpdatePeriod : WeatherConstants.SECONDS_FOR_ONE_HOUR) * 1000;
			long timeStampToFilter = currentTimeStamp - (weatherUpdatePeriod != null ? weatherUpdatePeriod : WeatherConstants.SECONDS_FOR_ONE_HOUR) * 1000;
			if (maxDate != null && currentTimeStamp <= timeStampRequired) {
				weatherDataTOs = getWeatherByLocation(location, weatherType, timeStampToFilter);
			} else {
				JSONObject fetchWeatherDataFromApi = weatherApiManager.fetchWeatherDataFromApi(location, weatherType);
				weatherDataTOs = buildWeatherDataTO(location, fetchWeatherDataFromApi, weatherType, maxDate != null ? maxDate.getTime() : 0, timeStampToFilter);
			}
		} catch (Exception e) {
			log.error("Exception occurred in getWeatherData...", e);
		}
		return weatherDataTOs;
	}

	/***
	 * Method to get maximum date of weather data
	 * 
	 * @param location
	 * @param weatherType
	 * @return Maximum date of weather data
	 * @author Praveen.K
	 * @since 1.0
	 */
	public Date getMaxDateByLocation(String location, String weatherType) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Date> query = cb.createQuery(Date.class);
		Root<WeatherDataTO> root = query.from(WeatherDataTO.class);
		Join<WeatherDataTO, LocationTO> locationJoin = root.join("location");
		query.select(cb.greatest(root.get("date")));
		query.where(cb.equal(locationJoin.get("locationName"), location), cb.equal(root.get("weatherType"), weatherType));
		TypedQuery<Date> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList().get(0);
	}

	/***
	 * Method to get list of WeatherDataTO based on the location filtered by calculated time stamp
	 * 
	 * @param location
	 * @param weatherType
	 * @param timestampToFilter
	 * @return list of WeatherDataTO
	 * @author Praveen.K
	 * @since 1.0
	 */
	private List<WeatherDataTO> getWeatherByLocation(String location, String weatherType, long timestampToFilter) {
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<WeatherDataTO> criteria = builder.createQuery(WeatherDataTO.class);
			Root<WeatherDataTO> root = criteria.from(WeatherDataTO.class);
			Join<WeatherDataTO, LocationTO> locationJoin = root.join("location");

			List<Predicate> predicates = new ArrayList<>();
			Predicate locationPredicate = builder.equal(locationJoin.get("locationName"), location);
			predicates.add(locationPredicate);
			Predicate dateRangePredicate = builder.greaterThanOrEqualTo(root.get("date"), new Date(timestampToFilter));
			predicates.add(dateRangePredicate);
			Predicate weatherTypePredicate = builder.equal(root.get("weatherType"), weatherType);
			predicates.add(weatherTypePredicate);

			criteria.where(builder.and(predicates.toArray(new Predicate[] {})));
			TypedQuery<WeatherDataTO> query = entityManager.createQuery(criteria);
			return query.getResultList();
		} catch (Exception e) {
			log.error("Exception occurred in getWeatherByLocation...", e);
		}
		return new ArrayList<>();
	}

	/***
	 * Method to get built WeatherDataTO by saving new Data and update existing Data
	 * 
	 * @param location
	 * @param fetchWeatherDataFromApi
	 * @param weatherType
	 * @param maxTimeStamp
	 * @param timeStampToFilter
	 * @return weatherDataTos, list of WeatherDataTO
	 * @throws ParseException
	 * @throws JSONException
	 * @author Praveen.K
	 * @since 1.0
	 */
	private List<WeatherDataTO> buildWeatherDataTO(String location, JSONObject fetchWeatherDataFromApi, String weatherType, long maxTimeStamp, long timeStampToFilter) throws ParseException, JSONException {
		List<WeatherDataTO> weatherDataTos = new ArrayList<>();
		try {
			weatherDataTos = getWeatherByLocation(location, weatherType, timeStampToFilter);

			Map<Long, WeatherDataTO> existingDataMap = new HashMap<>();
			if (WeatherConstants.WEATHER_TYPE_FORECAST.equalsIgnoreCase(weatherType)) {
				// Adding existing data to check duplicate dates and update
				for (WeatherDataTO existingRecord : weatherDataTos) {
					existingDataMap.put(existingRecord.getDate().getTime(), existingRecord);
				}
			}

			List<WeatherDataTO> existingDataToSave = new ArrayList<>();
			List<WeatherDataTO> newDataToSave = new ArrayList<>();

			if (fetchWeatherDataFromApi != null) {
				JSONObject currentWeatherJson = fetchWeatherDataFromApi;
				boolean isForecast = WeatherConstants.WEATHER_TYPE_FORECAST.equalsIgnoreCase(weatherType);
				if (isForecast) {
					currentWeatherJson = fetchWeatherDataFromApi.optJSONObject("city");
				}
				LocationTO locationTO = saveLocation(location, currentWeatherJson, isForecast);

				if (isForecast) {
					if (fetchWeatherDataFromApi.optJSONArray("list") != null) {
						JSONArray weatherDataList = fetchWeatherDataFromApi.optJSONArray("list");
						for (int i = 0; i < weatherDataList.length(); i++) {
							long timeStamp = ((JSONObject) weatherDataList.get(i)).optLong("dt") * 1000L;
							if (existingDataMap.get(timeStamp) != null) {
								WeatherDataTO existData = existingDataMap.get(timeStamp);
								existingDataToSave.add(existData);
							} else {
								WeatherDataTO weatherDataTO = buildWeatherData((JSONObject) weatherDataList.get(i), locationTO, weatherType);
								if (weatherDataTO != null) {
									newDataToSave.add(weatherDataTO);
								}
							}
						}
					}
				} else {
					long timeStamp = currentWeatherJson.optLong("dt") * 1000L;
					if (maxTimeStamp < timeStamp) {
						WeatherDataTO weatherDataTO = buildWeatherData(currentWeatherJson, locationTO, weatherType);
						if (weatherDataTO != null) {
							newDataToSave.add(weatherDataTO);
						}
					}
				}
			}
			// save new weather Data
			List<WeatherDataTO> savedTOs = null;
			if (!newDataToSave.isEmpty()) {
				savedTOs = weatherDataRepository.saveAll(newDataToSave);
				if (!savedTOs.isEmpty()) {
					weatherDataTos.addAll(savedTOs);
				}
			}
			// update new weather Data
			if (!existingDataToSave.isEmpty()) {
				savedTOs = weatherDataRepository.saveAll(existingDataToSave);
				if (!savedTOs.isEmpty()) {
					weatherDataTos.addAll(savedTOs);
				}
			}
		} catch (Exception e) {
			log.error("Exception occurred in buildWeatherDataTO...", e);
		}
		return weatherDataTos;
	}

	/***
	 * Method to save location
	 * 
	 * @param location
	 * @param currentWeatherJson
	 * @param isForecast
	 * @return locationTO
	 * @author Praveen.K
	 * @since 1.0
	 */
	private LocationTO saveLocation(String location, JSONObject currentWeatherJson, boolean isForecast) {
		LocationTO locationTO = locationRepository.findByLocationName(location);
		try {
			if (locationTO == null) {
				locationTO = new LocationTO();
				locationTO.setLocationName(currentWeatherJson.optString("name"));
				String coordinates = currentWeatherJson.optJSONObject("coord").optString("lon").concat(WeatherConstants.COORDINATE_SPLITTER).concat(currentWeatherJson.optJSONObject("coord").optString("lat"));
				locationTO.setCoordinates(coordinates);
				locationTO.setTimezone(currentWeatherJson.optInt("timezone"));
				locationTO.setCountry(isForecast ? currentWeatherJson.optString("country") : currentWeatherJson.optJSONObject("sys").optString("country"));
				// save location information
				locationRepository.save(locationTO);
			}
		} catch (Exception e) {
			log.error("Exception occurred in saveLocation...", e);
		}
		return locationTO;
	}

	/***
	 * Method to build WeatherData from fetched data from API
	 * 
	 * @param fetchWeatherDataFromApi
	 * @param locationTO
	 * @param weatherType
	 * @return weatherDataTO
	 * @author Praveen.K
	 * @since 1.0
	 */
	private WeatherDataTO buildWeatherData(JSONObject fetchWeatherDataFromApi, LocationTO locationTO, String weatherType) {
		try {
			WeatherDataTO weatherDataTO = new WeatherDataTO();
			weatherDataTO.setLocation(locationTO);
			weatherDataTO.setWeatherType(weatherType);
			JSONObject weather = fetchWeatherDataFromApi.optJSONArray("weather").optJSONObject(0);
			weatherDataTO.setWeather(weather.optString("main").concat(WeatherConstants.WEATHER_SPLITTER).concat(weather.optString("description")));
			if (fetchWeatherDataFromApi.optString("dt") != null) {
				weatherDataTO.setDate(DateUtils.getDate(Long.parseLong(fetchWeatherDataFromApi.optString("dt"))));
			}
			JSONObject main = fetchWeatherDataFromApi.optJSONObject("main");
			weatherDataTO.setTemperature(main.optString("temp"));
			weatherDataTO.setTemperatureMin(main.optString("temp_min"));
			weatherDataTO.setTemperatureMax(main.optString("temp_max"));
			weatherDataTO.setHumidity(main.optString("humidity"));
			weatherDataTO.setPressure(main.optString("pressure"));
			JSONObject wind = fetchWeatherDataFromApi.optJSONObject("wind");
			if (wind != null && (wind.has("speed") || wind.has("deg"))) {
				weatherDataTO.setWind(wind.optString("speed").concat(WeatherConstants.WIND_SPLITTER).concat(wind.optString("deg")));
			}
			JSONObject rain = fetchWeatherDataFromApi.optJSONObject("rain");
			if (rain != null && rain.has(WeatherConstants.WEATHER_IN_HOUR_THREE)) {
				weatherDataTO.setRain(WeatherConstants.WEATHER_IN_HOUR_THREE.concat(WeatherConstants.WEATHER_SPLITTER).concat(rain.optString(WeatherConstants.WEATHER_IN_HOUR_THREE)));
			}
			return weatherDataTO;
		} catch (NumberFormatException e) {
			log.error("Exception occurred in buildWeatherData...", e);
		}
		return null;
	}

}
