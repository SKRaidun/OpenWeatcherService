package ru.OpenWeather.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.OpenWeather.DAO.SessionDAO;
import ru.OpenWeather.models.Sessions;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class SessionValidator {

    private final SessionDAO sessionDAO;

    @Autowired
    public SessionValidator(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    public boolean hasValidSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Sessions session = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ID")) {
                session = sessionDAO.findSessionByID(cookie.getValue());
            }
        }

        if (session != null) {
            if (session.getExpiresAt().compareTo(Timestamp.valueOf(LocalDateTime.now())) < 0) {
                sessionDAO.deleteSessionById(session);
                return false;
            }
        } else {
            return false;
        }

            return true;
    }
}
