package ru.OpenWeather.DAO;

import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.OpenWeather.models.Sessions;
import ru.OpenWeather.models.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SessionDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public SessionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void createSession(User user) {
        Session session = sessionFactory.getCurrentSession();

        Sessions sessions = new Sessions();
        sessions.setUser(user);

        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime newExpireTime = dateTime.plusMinutes(30);
        Timestamp timestamp = Timestamp.valueOf(newExpireTime);
        sessions.setExpiresAt(timestamp);

        session.persist(sessions);
        user.setSession(sessions);
    }

    @Transactional
    public Sessions findSessionByID(String ID) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From Sessions s WHERE s.id = :SessionID";
        TypedQuery<Sessions> query = session.createQuery(hql, Sessions.class);
        query.setParameter("SessionID", UUID.fromString(ID));
        List<Sessions> sessions = query.getResultList();

        if (sessions.isEmpty()) {
            return null;
        }

        return sessions.get(0);
    }

    @Transactional
    public void deleteSessionById(Sessions currentSession) {
        Session session = sessionFactory.getCurrentSession();

        session.remove(currentSession);
    }



}
