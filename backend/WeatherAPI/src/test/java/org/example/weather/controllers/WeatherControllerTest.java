package org.example.weather.controllers;



import org.example.weather.models.WeatherData;
import org.example.weather.services.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebFluxTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeatherSuccess() throws Exception {
        WeatherData weatherData = new WeatherData();
        when(weatherService.getWeather(anyString())).thenReturn(Mono.just(weatherData));

        mockMvc.perform(MockMvcRequestBuilders.get("/weatherAPI/v1/getWeather/London").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header().string("X-Weather-API-Version", "1.0"))
                .andExpect(MockMvcResultMatchers.header().string("X-Weather-API-Status", "Success"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Language", "en-US"))
                .andExpect(MockMvcResultMatchers.header().string("X-RateLimit-Limit", "1000"))
                .andExpect(MockMvcResultMatchers.header().string("Accept-Language", "en-US"))
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "max-age=1200, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(MockMvcResultMatchers.header().string("ETag", String.valueOf(System.identityHashCode(weatherData))));
    }

    @Test
    void testGetWeatherClientError() throws Exception {
        when(weatherService.getWeather(anyString())).thenReturn(Mono.error(
                new WebClientResponseException(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null)));

        mockMvc.perform(MockMvcRequestBuilders.get("/weatherAPI/v1/getWeather/InvalidCity")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header().string("X-Weather-API-Version", "1.0"))
                .andExpect(MockMvcResultMatchers.header().string("X-Weather-API-Status", "Error"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Language", "en-US"))
                .andExpect(MockMvcResultMatchers.header().string("X-RateLimit-Limit", "1000"))
                .andExpect(MockMvcResultMatchers.header().string("Accept-Language", "en-US"))
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "max-age=1200, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Origin", "*"));

    }

    @Test
    void testGetWeatherServerError() throws Exception {
        when(weatherService.getWeather(anyString())).thenReturn(Mono.error(
                new WebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null, null, null)));

        mockMvc.perform(MockMvcRequestBuilders.get("/weatherAPI/v1/getWeather/London")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header().string("X-Weather-API-Version", "1.0"))
                .andExpect(MockMvcResultMatchers.header().string("X-Weather-API-Status", "Error"))
                .andExpect(MockMvcResultMatchers.header().string("Content-Language", "en-US"))
                .andExpect(MockMvcResultMatchers.header().string("X-RateLimit-Limit", "1000"))
                .andExpect(MockMvcResultMatchers.header().string("Accept-Language", "en-US"))
                .andExpect(MockMvcResultMatchers.header().string("Cache-Control", "max-age=1200, must-revalidate"))
                .andExpect(MockMvcResultMatchers.header().string("Access-Control-Allow-Origin", "*"));
    }
}