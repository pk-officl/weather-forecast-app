package in.pk.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.pk.weather.model.WeatherDataTO;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherDataTO, Long> {
    
}

