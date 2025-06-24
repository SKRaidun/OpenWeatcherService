package ru.OpenWeather.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login")
    @NotEmpty(message = "Login should not be empty")
    @Size(min = 2, max = 30, message = "Name length should be between 2 and 30")
    private String login;

    @Column(name = "password")
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 0, max = 30, message = "Password length should be more then ${min}")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Location> locations;

    @OneToOne(mappedBy = "user")
    private Sessions session;

    public User() {
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String password, List<Location> locations) {
        this.login = login;
        this.password = password;
        this.locations = locations;
    }

    public User(String login, String password, List<Location> locations, Sessions session) {
        this.login = login;
        this.password = password;
        this.locations = locations;
        this.session = session;
    }

    public Sessions getSession() {
        return session;
    }

    public void setSession(Sessions session) {
        this.session = session;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
