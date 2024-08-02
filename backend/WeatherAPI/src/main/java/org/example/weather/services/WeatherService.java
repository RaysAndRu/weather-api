package org.example.weather.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.weather.cache.WeatherCache;
import org.example.weather.models.Condition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.weather.models.CurrentWeather;
import org.example.weather.models.Location;
import org.example.weather.models.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.time.Duration;
import java.util.Map;

/**
 * Service class responsible for fetching and processing weather data from an external API.
 * It utilizes Spring WebFlux's {@link WebClient} for making HTTP requests and caching mechanisms to store fetched data.
 *
 * <p>This service provides methods to fetch current weather data for a specific city. It first checks if the data
 * is available in the cache. If the data is not cached, it retrieves the data from the external weather API,
 * updates the cache, and then returns the data.
 *
 * <p>In case of errors during data retrieval or processing, appropriate error handling and logging are performed.
 * This class uses the {@link WeatherCache} component for caching and the {@link WebClient} for making API requests.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Service
public class WeatherService {

    @Autowired
    WebClient webClient;

    @Autowired
    String token;

    @Autowired
    WeatherCache weatherCache;

    private static final Logger logger = LogManager.getLogger(WeatherService.class);

    /**
     * Fetches the current weather data for a given city.
     *
     * <p>This method first attempts to retrieve weather data from the cache using the provided city name. If the data is
     * not present in the cache, it fetches the data from an external weather API, stores it in the cache, and then returns
     * the data.
     *
     * <p>Logs are generated to indicate whether cached data is used or fresh data is fetched from the service.
     *
     * @param city The name of the city for which to fetch the weather data. Must be a non-null, non-empty string.
     * @return A {@link Mono<WeatherData>} that emits the {@link WeatherData} object containing the current weather
     *         data for the specified city. The Mono will complete normally if the data is successfully fetched and cached,
     *         or it will propagate an error if something goes wrong.
     * @throws RuntimeException if there is an error during the data retrieval process.
     */
    public Mono<WeatherData> getWeather(String city) {
        logger.info("Fetching weather data for: " + city);
        return weatherCache.get(city)
                .flatMap(data -> {
                    if (!data.isEmpty()) {
                        return Mono.just(data)
                                .doOnNext(log ->  logger.info("Returning cached weather data for city: " + city));
                    } else {
                        logger.info("No cached data found for city: " + city + ", fetching from service.");
                        return fetchFromService(city)
                                .doOnNext(log ->  logger.info("Returning cached weather data for city: " + city));
                    }
                })
                .doOnError(e -> logger.error("Failed to fetch weather data for {}: {}", city, e.getMessage()))
                .doOnNext(data -> logger.info("Data : " + data));
    }

    /**
     * Fetches the current weather data from an external weather API and updates the cache.
     *
     * <p>This method constructs a request URL using the city name and API token, performs an HTTP GET request to the weather
     * API, and processes the response. The response is converted into a {@link WeatherData} object, which is then cached
     * and returned.
     *
     * <p>If the HTTP request fails with a 4xx status code, a {@link RuntimeException} is thrown. If data conversion or
     * caching fails, appropriate logging is performed.
     *
     * @param city The name of the city for which to fetch the weather data. Must be a non-null, non-empty string.
     * @return A {@link Mono<WeatherData>} that emits the {@link WeatherData} object retrieved from the API and cached in
     *         Redis. The Mono will complete normally if the data is successfully fetched and cached, or it will propagate
     *         an error if something goes wrong.
     * @throws RuntimeException if the HTTP request to the API fails with a 4xx status code or if there is an error
     *                           during data conversion or caching.
     */
    public Mono<WeatherData> fetchFromService(String city) {
        String url = "http://api.weatherapi.com/v1/current.json?key=" + token + "&q=" + city;
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException("Request failed with status: 400" , clientResponse.statusCode()  ,clientResponse.toString()   , null, null, null)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException("Server error occurred", clientResponse.statusCode(),  clientResponse.toString(), null, null, null)))
                .bodyToMono(Map.class)
                .map(this::convertToWeatherData)
                .flatMap(weatherData -> weatherCache.put(city, weatherData, Duration.ofMinutes(60))
                        .thenReturn(weatherData))
                .doOnNext(data -> logger.info("Data has been pushed in cache: " + data));
    }

    /**
     * Converts the raw JSON response from the weather API into a structured {@link WeatherData} object.
     *
     * <p>This method parses the JSON response to extract weather information and maps it to the {@link WeatherData} model.
     * The response is expected to contain location and current weather details, which are used to create a {@link WeatherData}
     * object.
     *
     * @param data The raw JSON data received from the weather API. This is a map with string keys and object values
     *             representing the JSON structure.
     * @return A {@link WeatherData} object constructed from the provided JSON data. This object encapsulates weather
     *         information such as location details and current weather conditions.
     * @throws IllegalArgumentException if the JSON data does not conform to the expected structure or contains invalid values.
     */
    public WeatherData convertToWeatherData(Map<String, Object> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> currentMap = (Map<String, Object>) data.get("current");

        Location location = objectMapper.convertValue(data.get("location"), Location.class);
        Condition condition = objectMapper.convertValue(currentMap.get("condition"), Condition.class);
        CurrentWeather currentWeather = objectMapper.convertValue(data.get("current"), CurrentWeather.class);
        currentWeather.setCondition(condition);
        logger.info("JSON has been parsed");
        return new WeatherData(location, currentWeather);
    }
}
