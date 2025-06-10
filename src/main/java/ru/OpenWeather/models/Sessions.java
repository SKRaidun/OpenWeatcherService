package ru.OpenWeather.models;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "Sessions")
public class Sessions {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public void setId(UUID id) {
        this.id = id;
    }

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Sessions() {
    }

    public Sessions(User user) {
        this.user = user;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", expiresAt=" + expiresAt +
                ", user=" + user +
                '}';
    }
}
