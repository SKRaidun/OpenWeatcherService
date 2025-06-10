package ru.OpenWeather.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.OpenWeather.DAO.SessionDAO;
import ru.OpenWeather.DAO.UserDAO;
import ru.OpenWeather.models.Sessions;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@Controller
@RequestMapping("/weather-service")
public class LocationController {

    private final UserDAO dao;
    private final SessionDAO sessionDAO;
    private Sessions session;
    private final SessionValidator sessionValidator;

    @Autowired
    public LocationController(UserDAO dao, SessionDAO sessionDAO, SessionValidator sessionValidator) {
        this.dao = dao;
        this.sessionDAO = sessionDAO;
        this.sessionValidator = sessionValidator;
    }

    @GetMapping()
    public String login(HttpServletRequest request){
        if (!sessionValidator.hasValidSession(request)) {
            return "redirect:/login";
        }
        return "index";
    }
}
