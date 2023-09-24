package in.pk.weather.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "weather_data")
public class WeatherDataTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long weatherId;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationTO location;
    
    @Column(name = "weather_type", nullable = false, length = 500)
    private String weatherType;

    @Column(name = "weather", nullable = false, length = 500)
    private String weather;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "temperature")
    private String temperature;
    
    @Column(name = "temperature_min")
    private String temperatureMin;
    
    @Column(name = "temperature_max")
    private String temperatureMax;

    @Column(name = "humidity")
    private String humidity;

    @Column(name = "pressure")
    private String pressure;

    @Column(name = "wind")
    private String wind;

    @Column(name = "rain")
    private String rain;

	public Long getWeatherId() {
		return weatherId;
	}

	public void setWeatherId(Long weatherId) {
		this.weatherId = weatherId;
	}

	public LocationTO getLocation() {
		return location;
	}

	public void setLocation(LocationTO location) {
		this.location = location;
	}

	public String getWeatherType() {
		return weatherType;
	}
	
	public void setWeatherType(String weatherType) {
		this.weatherType = weatherType;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(String temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public String getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(String temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getPressure() {
		return pressure;
	}

	public void setPressure(String pressure) {
		this.pressure = pressure;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getRain() {
		return rain;
	}

	public void setRain(String rain) {
		this.rain = rain;
	}
    
}

