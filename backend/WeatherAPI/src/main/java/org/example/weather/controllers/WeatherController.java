package org.example.weather.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.weather.models.WeatherData;

import org.example.weather.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * Controller class responsible for handling HTTP requests related to weather data.
 * It provides an endpoint to fetch weather information based on city names.
 *
 * <p>This controller exposes an API endpoint to retrieve the current weather data for a given city. It relies on
 * the {@link WeatherService} to fetch the data and returns it wrapped in a {@link ResponseEntity} with appropriate
 * HTTP headers and status codes.
 *
 * <p>The API uses the Swagger annotations to document the endpoint, responses, and request parameters.
 */
@RestController
@RequestMapping("/weatherAPI/v1")
public class WeatherController {

    /**
     * Service class that handles business logic related to fetching weather data.
     */
    @Autowired
    WeatherService weatherService;

    /**
     * Endpoint to retrieve weather data for a specific city.
     *
     * <p>This method handles GET requests to retrieve the current weather data for the specified city.
     * It uses the {@link WeatherService} to fetch the weather data and returns it in the response body. If the city
     * is not found, a 404 status code is returned. If there is an issue with the API token or other internal errors
     * occur, appropriate status codes are returned.
     *
     * @param city The name of the city for which to fetch weather data. It must be a non-null, non-empty string.
     * @return A {@link Mono<ResponseEntity<WeatherData>>} that emits a {@link ResponseEntity} containing the weather data
     *         if found. The response will include HTTP headers and status codes indicating success or failure.
     *
     * <p>Success response (200 OK) includes weather data for the city.
     * <p>Error responses:
     * <ul>
     *     <li>404 (Not Found) if the city is not found.</li>
     *     <li>403 (Forbidden) if the API token is invalid.</li>
     *     <li>500 (Internal Server Error) for any unexpected errors.</li>
     * </ul>
     */
    @Operation(
            tags = "getWeatherApi",
            description = "Get weather API",
            summary = "Get weather information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WeatherData.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{\"location\":{\"name\":\"Saratov\",\"region\":\"Saratov\",\"country\":\"Russia\",\"lat\":51.57,\"lon\":46.03,\"tz_id\":\"Europe/Saratov\",\"localtime_epoch\":1722349059,\"localtime\":\"2024-07-30 18:17\"},\"currentWeather\":{\"last_updated_epoch\":1722348900,\"last_updated\":\"2024-07-30 18:15\",\"temp_c\":24.5,\"temp_f\":76.2,\"is_day\":1,\"condition\":{\"text\":\"Sunny\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/113.png\",\"code\":1000},\"wind_mph\":9.8,\"wind_kph\":15.8,\"wind_degree\":185,\"wind_dir\":\"S\",\"pressure_mb\":998.0,\"pressure_in\":29.47,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":42,\"cloud\":10,\"feelslike_c\":25.2,\"feelslike_f\":77.3,\"windchill_c\":24.5,\"windchill_f\":76.2,\"heatindex_c\":25.2,\"heatindex_f\":77.3,\"dewpoint_c\":10.7,\"dewpoint_f\":51.3,\"vis_km\":10.0,\"vis_miles\":6.0,\"uv\":6.0,\"gust_mph\":19.8,\"gust_kph\":31.8},\"empty\":false}"
                                    )
                            }
                    )),
                    @ApiResponse(responseCode = "400", description = "City not found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{\"statusCode\":\"BAD_REQUEST\",\"body\":{\"type\":\"about:blank\",\"title\":\"ERROR\",\"status\":400,\"detail\":\"An error occurred: Request failed with status: 400 BAD_REQUEST\"},\"headers\":{},\"typeMessageCode\":\"problemDetail.type.org.example.weather.exceptions.WeatherResponseError\",\"titleMessageCode\":\"problemDetail.title.org.example.weather.exceptions.WeatherResponseError\",\"detailMessageCode\":\"problemDetail.org.example.weather.exceptions.WeatherResponseError\",\"detailMessageArguments\":null}"
                                    )

                            }
                    )),
                    @ApiResponse(responseCode = "500", description = "Internal error", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{\"statusCode\":\"INTERNAL_SERVER_ERROR\",\"body\":{\"type\":\"about:blank\",\"title\":\"ERROR\",\"status\":500,\"detail\":\"An error occurred: Request failed with status: 500 INTERNAL_SERVER_ERROR\"},\"headers\":{},\"typeMessageCode\":\"problemDetail.type.org.example.weather.exceptions.WeatherResponseError\",\"titleMessageCode\":\"problemDetail.title.org.example.weather.exceptions.WeatherResponseError\",\"detailMessageCode\":\"problemDetail.org.example.weather.exceptions.WeatherResponseError\",\"detailMessageArguments\":null}"
                                    )
                            }
                    ))
            }

    )
    @GetMapping("/getWeather/{city}")
    public Mono<ResponseEntity<WeatherData>> getWeather(@PathVariable String city) {
        return weatherService.getWeather(city)
                .map(data -> ResponseEntity.ok()
                        .header("X-Weather-API-Version", "1.0")
                        .header("X-Weather-API-Status", "Success")
                        .header("Content-Language", "en-US")
                        .header("X-RateLimit-Limit", "1000")
                        .header("Accept-Language", "en-US")
                        .header("Cache-Control", "max-age=1200, must-revalidate")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("ETag", String.valueOf(System.identityHashCode(data)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentLength(data.toString().getBytes().length)
                        .body(data));
    }
}