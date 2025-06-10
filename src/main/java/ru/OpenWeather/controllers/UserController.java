package ru.OpenWeather.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.OpenWeather.DAO.SessionDAO;
import ru.OpenWeather.DAO.UserDAO;
import ru.OpenWeather.DTO.UserValidator;
import ru.OpenWeather.models.Sessions;
import ru.OpenWeather.models.User;

@Component
@Controller
@RequestMapping("/login")
public class UserController {

    private final UserDAO dao;
    private final SessionDAO sessionDAO;
    private final SessionValidator sessionValidator;
    private  Sessions session;

    @Autowired
    public UserController(UserDAO DAO, SessionDAO sessionDAO, SessionValidator sessionValidator) {
        this.dao = DAO;
        this.sessionDAO = sessionDAO;
        this.sessionValidator = sessionValidator;
    }

    private HttpServletResponse setCookie(Sessions session, HttpServletResponse response) {
        Cookie cookie = new Cookie("ID", String.valueOf(session.getId()));
        response.addCookie(cookie);
        return response;
    }

    @GetMapping()
    public String login(HttpServletRequest request){
        if (sessionValidator.hasValidSession(request)) {
            return "redirect:/weather-service";
        }
        return "sign-in";
    }

    @PostMapping()
    public String signIn(Model model, @ModelAttribute("user") User user, HttpServletResponse response) {

        model.addAttribute("user");

        String userLogin = user.getLogin();
        String password = user.getPassword();

        User loginUser = dao.findUser(userLogin, password);
        if(loginUser == null) {
            return "sign-in-with-errors";
        }

        if (dao.findSessionByID(loginUser) == null) {
            sessionDAO.createSession(loginUser);
        }

        System.out.println(loginUser.getSession().getId());
        System.out.println(loginUser.getSession().getExpiresAt());

        HttpServletResponse newResponse = setCookie(loginUser.getSession(), response);

        return "redirect:/weather-service";
    }

    @GetMapping("/signup")
    public String signUp(HttpServletRequest request) {
        if (sessionValidator.hasValidSession(request)) {
            return "redirect:/login";
        }
        return "sign-up";
    }

    @PostMapping("/signup")
    private String signUpValidation(Model model, @ModelAttribute("user") UserValidator userValidator) {

        model.addAttribute("user");

        String userLogin = userValidator.getLogin();
        String password = userValidator.getPassword();
        String repeatPassword = userValidator.getRepeatPassword();

        if (!password.equals(repeatPassword) | !dao.findLogin(userLogin)) {
            return "sign-up-with-errors";
        }

        User user = new User(userLogin, password);
        dao.createUser(user);

        return "redirect:/login";
    }
}

