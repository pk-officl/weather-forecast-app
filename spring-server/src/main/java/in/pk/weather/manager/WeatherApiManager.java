package in.pk.weather.manager;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.pk.weather.config.WeatherApiConfig;

@Component	
public class WeatherApiManager {

	private static final Logger log = LoggerFactory.getLogger(WeatherApiManager.class);
	
	private final WeatherApiConfig weatherApiConfig;

	// injecting required class using constructor to make sure it is not null before using it in this class
	// and testing convenience 
    @Autowired
    public WeatherApiManager(WeatherApiConfig weatherApiConfig) {
        this.weatherApiConfig = weatherApiConfig;
    }
	
    /***
     * Method to get fetch weather API data and call the API
     * 
     * @param location
     * @param weatherType
     * @return JSON Object
     * @author Praveen.K
     * @since 1.0
     */
	public JSONObject fetchWeatherDataFromApi(String location, String weatherType) {
		log.info("Entering into fetchWeatherDataFromApi with location : {}", location);
		JSONObject resJson = null;
		try {
			Map<String, Map<String, String>> configMaps = weatherApiConfig.getWeatherConfig();
			if(configMaps!=null && configMaps.get(weatherType)!=null) {
				Map<String, String> configMap =  configMaps.get(weatherType);
				String baseUrl = configMap.get("url");
				String apiKey = configMap.get("apiKey");
				String responseString = getResponse(location, baseUrl, apiKey);
				if(StringUtils.isNotBlank(responseString)) {
					resJson = new JSONObject(responseString);
				}
			}
		} catch (Exception e) {
			log.error("Exception occurred in fetchWeatherDataFromApi...", e);
		}
		return resJson;
	}

	/***
	 * Method to make API call with respective data and get response
	 * 
	 * @param location
	 * @param baseUrl
	 * @param apiKey
	 * @return Response as String
	 * @throws IOException
	 * @author Praveen.K
	 * @since 1.0
	 */
	private String getResponse(String location, String baseUrl, String apiKey) {
		String responseString = "";
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			String apiUrl = baseUrl + "?q=" + location + "&appid=" + apiKey;
			HttpGet request = new HttpGet(apiUrl);
			HttpResponse response = httpClient.execute(request);
			if (response.getEntity() != null) {
				responseString = EntityUtils.toString(response.getEntity());
			}
			log.info("response : {}", response.getStatusLine());
		} catch (IOException e) {
			log.error("Exception occurred in getResponse...", e);
		}
		return responseString;
	}

}
