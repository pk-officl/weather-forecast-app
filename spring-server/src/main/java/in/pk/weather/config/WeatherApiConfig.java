package in.pk.weather.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import in.pk.weather.model.WeatherConfigTO;
import in.pk.weather.repository.WeatherConfigRepository;

@Configuration
public class WeatherApiConfig {

	private static final Logger log = LoggerFactory.getLogger(WeatherApiConfig.class);
	
	@Value("${weather.source.name}")
	private String sourceName;

	@Autowired
	private WeatherConfigRepository weatherConfigRepository;

	private Map<String, Map<String, String>> configTOMap;

	@PostConstruct
	private void initializeConfig() {
		// Load the config data from the repository during application startup
		configTOMap = new HashMap<>();
		List<WeatherConfigTO> configTOs = weatherConfigRepository.findByConfigGroup(sourceName);
		if (configTOs != null && !configTOs.isEmpty()) {
			for (WeatherConfigTO configTO : configTOs) {
				Map<String, String> map;
				if (configTOMap.get(configTO.getConfigType()) != null) {
					map = configTOMap.get(configTO.getConfigType());
					map.put(configTO.getConfigName(), configTO.getConfigValue());
				} else {
					map = new HashMap<>();
					map.put(configTO.getConfigName(), configTO.getConfigValue());
					configTOMap.put(configTO.getConfigType(), map);
				}
			}
		}
		log.info(" Weather API configuration has been initialized... {}", configTOMap);
	}

	public Map<String, Map<String, String>> getWeatherConfig() {
		if(configTOMap==null) {
			initializeConfig();
		}
		return configTOMap;
	}
}
