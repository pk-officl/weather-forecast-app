import './App.css';
import React, { useState } from 'react';
import bg from './public/weather-background.jpg'
import axios from 'axios';
import config from './config/config.json';

function App() {
  const [inputValue, setInputValue] = useState("");
  const [selectedType, setSelectedType] = useState("current");
  const [weatherData, setWeatherData] = useState([]);

  const handleInputChange = (e) => {
    setInputValue(e.target.value)
  }
  const handleSelectType = (e) => {
    setSelectedType(e.target.value);
  }

  const getWeatherData = () => {
    try {
      const API = axios.create({
        baseURL: config.API_BASE_URL,
      });
      API.get("/weather-rest/weather?location=" + inputValue + "&weatherType=" + selectedType)
        .then(response => {
          if (selectedType === 'current') {
            if (response?.data?.current !== undefined) {
              var currentWeather = [];
              currentWeather.push(response.data[selectedType])
              setWeatherData(currentWeather);
            } else {
              setWeatherData([]);
            }
          } else {
            if (response?.data?.forecast !== undefined) {
              setWeatherData(response.data[selectedType]);
            } else {
              setWeatherData([]);
            }
          }
        })
        .catch(error => {
          console.log(error);
        })
    } catch (error) {
      console.log(error);
    }
  }

  return (
    <div className="App">
      <div className='header'> Zerp Weather App </div>
      <div className='weather-cont'>
        <img className='bg-img' src={bg}></img>
        <div className='search-bar'>
          <div className='search-bar-cont'>
            <div className='search'>
              <input className='search-input' type='text' value={inputValue} onChange={handleInputChange} placeholder='search by city' ></input>
            </div>
            <div className='search-btn'>
              <button onClick={getWeatherData} > Search</button>
            </div>
          </div>
        </div>
        <div className='weather-type-cont'>
          <div className='weather-type'>
            <div>
              <input type='radio' name='radio' checked={selectedType == "current"} value="current" onChange={handleSelectType} ></input>
              <label>Current Weather</label>
            </div>
            <div>
              <input type='radio' name='radio' checked={selectedType == "forecast"} value="forecast" onChange={handleSelectType} ></input>
              <label>Forecast</label>
            </div>
          </div>
        </div>
        <div className=''>

        <div className='weather-card-cont'>
          {weatherData !== undefined && weatherData.length > 0 &&
            weatherData.map((data, index) => (
              <div className='weather-card'>
                <div className='weather' key={index}>
                  <div className='item' >
                    <label>Weather</label>
                    <span> {data.weather}</span>
                  </div>
                  <div className='item' >
                    <label>Description</label>
                    <span> {data.description}</span>
                  </div>
                  <div>
                    <label>Date</label>
                    <span>{data.date}</span>
                  </div>
                </div>
                <div className='weather' key={index}>
                  <div className='item' >
                    <label>Temperature</label>
                    <span> {data.temperature} ({data.temperature_min} - {data.temperature_max})</span>
                  </div>
                  <div className='item' >
                    <label>Humidity</label>
                    <span> {data.humidity}</span>
                  </div>
                  <div>
                    <label>Wind</label>
                    <span>{data.wind_speed} / {data.wind_deg}</span>
                  </div>
                </div>
                <div className='weather' key={index}>
                  <div className='item' >
                    <label>Pressure</label>
                    <span> {data.pressure}</span>
                  </div>
                  <div className='item' >
                    <label>Location/City</label>
                    <span> {data.location}</span>
                  </div>
                  <div>
                    <label>Country</label>
                    <span>{data.country}</span>
                  </div>
                </div>
                
              </div>
            ))
          }
        </div>
        </div>
      </div>
    </div>
  );
}

export default App;
