package org.example.weather.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherConfig {

    @Bean
    public String weatherToken() {
         //API token from https://www.weatherapi.com/
    }
}
