package ru.OpenWeather.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.OpenWeather.DAO.LocationDAO;
import ru.OpenWeather.DAO.SessionDAO;
import ru.OpenWeather.DAO.UserDAO;
import ru.OpenWeather.DTO.UserValidator;
import ru.OpenWeather.models.Location;
import ru.OpenWeather.models.Sessions;
import ru.OpenWeather.models.User;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Controller
@RequestMapping("/weather-service")
public class LocationController {

    private final UserDAO dao;
    private final SessionDAO sessionDAO;
    private  final LocationDAO locationDAO;
    private Sessions session;
    private final SessionValidator sessionValidator;
    private final LocationValidator locationValidator;

    @Autowired
    public LocationController(UserDAO dao, SessionDAO sessionDAO, LocationDAO locationDAO, SessionValidator sessionValidator, LocationValidator locationValidator) {
        this.dao = dao;
        this.sessionDAO = sessionDAO;
        this.locationDAO = locationDAO;
        this.sessionValidator = sessionValidator;
        this.locationValidator = locationValidator;
    }



    @GetMapping()
    public String login(Model model, HttpServletRequest request){
        if (!sessionValidator.hasValidSession(request)) {
            return "redirect:/login";
        }

        List<Location> locations = null;

        User user = sessionValidator.findUserBySession(request);
        try {
            locations = locationDAO.findLocationsByUserId(user.getId());
        } catch (NullPointerException e) {}

        model.addAttribute("locations", locations);
        return "index";
    }


    @PostMapping()
    private String addLocation(Model model, @ModelAttribute("location") String location, HttpServletRequest request) throws IOException, InterruptedException {
        Location newLocation = locationValidator.findLocationWithAPI(location);
        User user = sessionValidator.findUserBySession(request);
        String locationName = newLocation.getName();
        int userID = user.getId();
        if (locationDAO.findLocationByNameAndId(locationName, userID)) return "redirect:/login";
        newLocation.setUser(user);
        locationDAO.createLocation(newLocation);

        return "redirect:/login";
    }
}
