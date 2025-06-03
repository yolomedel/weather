package com.weatherData.weather.service;

import com.weatherData.weather.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String apiUrl;

    private String city = "Friedrichshafen"; // Default city

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public WeatherData getWeatherData() {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            
            if (response == null || response.getMain() == null) {
                return null;
            }
            
            // Extract all weather data
            Double temperature = response.getMain().getTemp();
            Integer humidity = response.getMain().getHumidity();
            Double windSpeed = (response.getWind() != null) ? response.getWind().getSpeed() : 0.0;
            
            return new WeatherData(
                city,
                temperature,
                humidity,
                windSpeed,
                "°C",
                "m/s",
                "%"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public record WeatherData(
        String city,
        Double temperature,
        Integer humidity,
        Double windSpeed,
        String tempUnit,
        String speedUnit,
        String humidityUnit
    ) {}
}