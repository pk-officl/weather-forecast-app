package in.pk.weather.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.pk.weather.model.WeatherConfigTO;

public interface WeatherConfigRepository extends JpaRepository<WeatherConfigTO, Long> {

	List<WeatherConfigTO> findByConfigGroup(String groupName);

}
