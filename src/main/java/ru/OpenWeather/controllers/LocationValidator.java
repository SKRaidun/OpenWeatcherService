package ru.OpenWeather.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.OpenWeather.models.Location;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



@Component
public class LocationValidator {

    private final String apiToken;

    @Autowired
    public LocationValidator(@Value("${weather.api.token}") String apiToken) {
        this.apiToken = apiToken;
    }


    private Location parseAPIResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();

        Location location = new Location();
        double convertionCoef = 273.15;
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            if (rootNode.get("cod").asText().equals("404")) return null;
            String name = rootNode.get("name").asText();
            double lon = rootNode.get("coord").get("lon").asDouble();
            double lat = rootNode.get("coord").get("lat").asDouble();
            double temperature = Math.round(rootNode.get("main").get("temp").asDouble() - convertionCoef);
            double feelsLike = Math.round(rootNode.get("main").get("feels_like").asDouble() - convertionCoef);
            String description = rootNode.get("weather").get(0).get("description").asText();
            double humidity = rootNode.get("main").get("humidity").asDouble();
            location.setName(name);
            location.setLatitude(lat);
            location.setLongitude(lon);
            location.setTemperature(temperature);
            location.setFeelsLike(feelsLike);
            location.setDescription(description);
            location.setHumidity(humidity);

            System.out.println(location.getTemperature());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return location;
    }

    public Location findLocationWithAPI(String location) throws IOException, InterruptedException {

        String URL = "https://api.openweathermap.org/data/2.5/weather?q=";
        String fullURI = URL +
                location +
                "&appid=" +
                apiToken;
        URI uriObj = URI.create(fullURI);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriObj)
                .GET()
                .build();


        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        String responseBodyJson = response.body();

        Location newLocation = parseAPIResponse(responseBodyJson);

        return newLocation;
    }
}
