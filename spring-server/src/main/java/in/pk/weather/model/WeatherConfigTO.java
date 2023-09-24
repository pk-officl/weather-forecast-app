package in.pk.weather.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="weather_config")
public class WeatherConfigTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "config_id")
	private Long configId;

	@Column(name = "config_type")
	private String configType;
	
	@Column(name = "config_group_name")
	private String configGroup;

	@Column(name = "config_name")
	private String configName;

	@Column(name = "config_value")
	private String configValue;

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	
	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getConfigGroup() {
		return configGroup;
	}

	public void setConfigGroup(String configGroup) {
		this.configGroup = configGroup;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	
}
