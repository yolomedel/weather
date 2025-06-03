package com.weatherData.weather.controller;

import com.weatherData.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getWeather() {
        WeatherService.WeatherData weatherData = weatherService.getWeatherData();
        if (weatherData == null) {
            return ResponseEntity.internalServerError().body("Failed to fetch weather data");
        }
        return ResponseEntity.ok(weatherData);
    }

    @PostMapping("/city")
    public ResponseEntity<String> setCity(@RequestBody Map<String, String> request) {
        String newCity = request.get("city");
        if (newCity == null || newCity.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("City name cannot be empty");
        }
        
        try {
            weatherService.setCity(newCity);
            WeatherService.WeatherData data = weatherService.getWeatherData();
            if (data == null) {
                return ResponseEntity.badRequest().body("Failed to get weather for " + newCity);
            }
            return ResponseEntity.ok("City updated to " + newCity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating city: " + e.getMessage());
        }
    }
}