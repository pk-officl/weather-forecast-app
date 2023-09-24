package in.pk.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.pk.weather.model.LocationTO;

@Repository
public interface LocationRepository extends JpaRepository<LocationTO, Long> {

	LocationTO findByLocationName(String location);
    
}

