package org.example.weather.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.weather.controllers.WeatherController;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Mono;



@ControllerAdvice(basePackageClasses = WeatherController.class)
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = HttpClientErrorException.class)
    public final Mono<ResponseEntity<Object>> handleHttpClientErrorException(HttpClientErrorException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("ERROR");
        problemDetail.setDetail("An error occurred: " + ex.getMessage());

        ErrorResponse errorResponse = new WeatherResponseError(HttpStatus.BAD_REQUEST, problemDetail);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(errorResponse.toString().getBytes().length)
                .header("X-Weather-API-Version", "1.0")
                .header("X-Weather-API-Status", "Error")
                .header("X-Error-Code", "400" )
                .header("Retry-After", "60")
                .header("Vary", "Accept-Encoding")
                .header("Cache-Control", "no-store")
                .header("Access-Control-Allow-Origin", "*")
                .body(errorResponse);
        return Mono.just(responseEntity);
    }

    @ExceptionHandler(value = HttpServerErrorException.class)
    public final Mono<ResponseEntity<Object>> handleHttpServerErrorException(HttpServerErrorException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("ERROR");
        problemDetail.setDetail("An error occurred: " + ex.getMessage());

        ErrorResponse errorResponse = new WeatherResponseError(HttpStatus.INTERNAL_SERVER_ERROR, problemDetail);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(errorResponse.toString().getBytes().length)
                .header("X-Weather-API-Version", "1.0")
                .header("X-Weather-API-Status", "Error")
                .header("X-Error-Code", "500" )
                .header("Retry-After", "60")
                .header("Vary", "Accept-Encoding")
                .header("Cache-Control", "no-store")
                .header("Access-Control-Allow-Origin", "*")
                .body(errorResponse);
        return Mono.just(responseEntity);
    }

    @ExceptionHandler(value = Exception.class)
    public final Mono<ResponseEntity<Object>> handleServerErrorException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("ERROR");
        problemDetail.setDetail("An error occurred: " + ex.getMessage());

        ErrorResponse errorResponse = new WeatherResponseError(HttpStatus.INTERNAL_SERVER_ERROR, problemDetail);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(errorResponse.toString().getBytes().length)
                .header("X-Weather-API-Version", "1.0")
                .header("X-Weather-API-Status", "Error")
                .header("X-Error-Code", "500" )
                .header("Retry-After", "60")
                .header("Vary", "Accept-Encoding")
                .header("Cache-Control", "no-store")
                .header("Access-Control-Allow-Origin", "*")
                .body(errorResponse);
        return Mono.just(responseEntity);
    }

}