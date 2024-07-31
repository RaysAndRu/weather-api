package org.example.weather.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    Location location;
    CurrentWeather currentWeather;

    public boolean isEmpty(){
        return location == null && currentWeather == null;
    }
}
