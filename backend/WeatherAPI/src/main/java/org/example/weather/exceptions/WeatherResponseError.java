package org.example.weather.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;



@AllArgsConstructor
public class WeatherResponseError implements ErrorResponse {

    private HttpStatusCode statusCode;
    private ProblemDetail message;


    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return message;
    }


}
