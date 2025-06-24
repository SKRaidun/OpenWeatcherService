package ru.OpenWeather.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.OpenWeather.DAO.SessionDAO;
import ru.OpenWeather.DAO.UserDAO;
import ru.OpenWeather.models.Sessions;
import ru.OpenWeather.models.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class SessionValidator {

    private final SessionDAO sessionDAO;
    private final UserDAO userDAO;

    @Autowired
    public SessionValidator(SessionDAO sessionDAO, UserDAO userDAO) {
        this.sessionDAO = sessionDAO;
        this.userDAO = userDAO;
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


    public User findUserBySession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Sessions session = null;
        User loggedUser = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ID")) {
                session = sessionDAO.findSessionByID(cookie.getValue());
            }

            try {
                loggedUser = userDAO.findUserByID(session.getUser().getId());
            } catch (NullPointerException ignored) {
            }
        }
        return loggedUser;
    }
}
