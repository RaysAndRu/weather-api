package org.example.weather.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.weather.controllers.WeatherController;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice(basePackageClasses = WeatherController.class)
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = RuntimeException.class)
    public final Mono<ResponseEntity<Object>> handleAllExceptions(Exception ex) {
        String errorCode = determineErrorCode(ex);
        HttpStatus httpStatus = mapErrorCodeToHttpStatus(errorCode);

        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle("ERROR");
        problemDetail.setDetail("An error occurred: " + ex.getMessage());

        ErrorResponse errorResponse = new WeatherResponseError(httpStatus, problemDetail);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(errorResponse.toString().getBytes().length)
                .header("X-Weather-API-Version", "1.0")
                .header("X-Weather-API-Status", "Error")
                .header("X-Error-Code", errorCode)
                .header("Retry-After", "60")
                .header("Vary", "Accept-Encoding")
                .header("Cache-Control", "no-store")
                .header("Access-Control-Allow-Origin", "*")
                .body(errorResponse);
        return Mono.just(responseEntity);
    }


    private String determineErrorCode(Exception ex) {

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(ex.getMessage());
            String code;
            if (matcher.find()) {
                code = matcher.group();
            } else {
                code = "no code";
            }
            logger.info("Status code of request " + code );
            return code;
        }


    private HttpStatus mapErrorCodeToHttpStatus(String errorCode) {
            String firstDigit = errorCode.substring(0, 1);
            switch (firstDigit) {
                case "4":
                    return HttpStatus.BAD_REQUEST;
                default:
                    return HttpStatus.INTERNAL_SERVER_ERROR;
            }
    }
}