
-- Create the database if it doesn't exist
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'weather_forecast')
BEGIN
    CREATE DATABASE weather_forecast;
END
GO

-- Use the created database
USE weather_forecast;
GO

-- Create the location table
CREATE TABLE location (
    location_id INT IDENTITY(1,1) PRIMARY KEY,
    location_name NVARCHAR(256) NOT NULL,
	coordinates NVARCHAR (256) NOT NULL,
	timezone INT,
	country NVARCHAR (256)
);
GO

-- Create the weather_data table with a foreign key reference to location
CREATE TABLE weather_data (
    weather_id INT IDENTITY(1,1) PRIMARY KEY,
	weather_type NVARCHAR(256) NOT NULL,
	location_id INT,
	weather NVARCHAR(500) NOT NULL,
    date DATETIME NOT NULL,
    temperature NVARCHAR(100),
	temperature_min NVARCHAR(100),
	temperature_max NVARCHAR(100),
    humidity NVARCHAR(100),
	pressure NVARCHAR(100),
	wind NVARCHAR(100),
	rain NVARCHAR(100),
    FOREIGN KEY (location_id) REFERENCES location(location_id)
);
GO

-- Create the WeatherConfig table
CREATE TABLE weather_config (
    config_id INT IDENTITY(1,1) PRIMARY KEY,
    config_group_name NVARCHAR(256) NOT NULL,
	config_type NVARCHAR(256) NOT NULL,
	config_name NVARCHAR (256) NOT NULL,
	config_value NVARCHAR(1000) NOT NULL
);
GO

--Adding weather API configuration details

insert into weather_config (config_group_name,config_type,config_name,config_value)
values ('openweathermap','current','url','https://api.openweathermap.org/data/2.5/weather');

insert into weather_config (config_group_name,config_type,config_name,config_value)
values ('openweathermap','current','apiKey','ca87f1c449c0c01c085040278cf572b9');

insert into weather_config (config_group_name,config_type,config_name,config_value)
values ('openweathermap','forecast','url','https://api.openweathermap.org/data/2.5/forecast');

insert into weather_config (config_group_name,config_type,config_name,config_value)
values ('openweathermap','forecast','apiKey','ca87f1c449c0c01c085040278cf572b9');
