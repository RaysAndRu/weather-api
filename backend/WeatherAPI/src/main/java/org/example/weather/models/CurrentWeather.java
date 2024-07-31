package org.example.weather.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWeather {
    @JsonProperty("last_updated_epoch")
    private long lastUpdatedEpoch;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("temp_c")
    private double tempC;

    @JsonProperty("temp_f")
    private double tempF;

    @JsonProperty("is_day")
    private int isDay;

    @JsonProperty("condition")
    private Condition condition;


    @JsonProperty("wind_mph")
    private double windMph;

    @JsonProperty("wind_kph")
    private double windKph;

    @JsonProperty("wind_degree")
    private int windDegree;

    @JsonProperty("wind_dir")
    private String windDir;

    @JsonProperty("pressure_mb")
    private double pressureMb;

    @JsonProperty("pressure_in")
    private double pressureIn;

    @JsonProperty("precip_mm")
    private double precipMm;

    @JsonProperty("precip_in")
    private double precipIn;

    @JsonProperty("humidity")
    private int humidity;

    @JsonProperty("cloud")
    private int cloud;

    @JsonProperty("feelslike_c")
    private double feelsLikeC;

    @JsonProperty("feelslike_f")
    private double feelsLikeF;

    @JsonProperty("windchill_c")
    private double windChillC;

    @JsonProperty("windchill_f")
    private double windChillF;

    @JsonProperty("heatindex_c")
    private double heatIndexC;

    @JsonProperty("heatindex_f")
    private double heatIndexF;

    @JsonProperty("dewpoint_c")
    private double dewPointC;

    @JsonProperty("dewpoint_f")
    private double dewPointF;

    @JsonProperty("vis_km")
    private double visKm;

    @JsonProperty("vis_miles")
    private double visMiles;

    @JsonProperty("uv")
    private double uv;

    @JsonProperty("gust_mph")
    private double gustMph;

    @JsonProperty("gust_kph")
    private double gustKph;

}

