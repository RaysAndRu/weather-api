import React from 'react';
import style from '../styles/style.css';



const WeatherDisplay = ({ weatherData }) => {
  console.log("Temp: " + weatherData.location.temp_c)
  return (
    <div className="container-details">
      <h2 className="title">Weather</h2>
      <section className="location-info">
        <h3>Location</h3>
        <p >Country: {weatherData.location.country}</p>
        <p>Region: {weatherData.location.region}</p>
        <p>City: {weatherData.location.name}</p>
        <p>Localtime: {weatherData.location.localtime}</p>
      </section>
      <section className="current-conditions"> 
          <h3>Current Conditions</h3>
          <img alt ="weather"src={weatherData.currentWeather.condition.icon}></img>
          <p>Day/Night: {weatherData.currentWeather.is_day === 1 ? 'day' : 'night'}</p>
          <p>Temperature: {Math.round( weatherData.currentWeather.temp_c) }°C</p>
          <p>Description: {weatherData.currentWeather.condition.text}</p>
          <p>Wind: {Math.round(weatherData.currentWeather.wind_kph)} kph</p>
          <p>Wind: {weatherData.currentWeather.wind_dir} dir</p>
          <p>Pressure: {Math.round(weatherData.currentWeather.pressure_mb)} mb</p>
          <p>Humidity: {weatherData.currentWeather.humidity}%</p>
          <p>Cloud: {weatherData.currentWeather.cloud}%</p>
          <p>Feelslike: {Math.round(weatherData.currentWeather.feelslike_c)}°C</p>
          <p>Visibility: {Math.round(weatherData.currentWeather.vis_km)} kph</p>
          <p>UV: {Math.round(weatherData.currentWeather.uv)}</p>
          <p>Last updated: {weatherData.currentWeather.last_updated}</p>
      </section>
    </div>
  );
};

export default WeatherDisplay;
